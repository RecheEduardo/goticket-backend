package tech.goticket.backendapi.order.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.client.Client;
import tech.goticket.backendapi.client.ClientRepository;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.repository.EventDateRepository;
import tech.goticket.backendapi.fee.dto.FeeBreakdown;
import tech.goticket.backendapi.fee.service.FeeCalculator;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.OrderItem;
import tech.goticket.backendapi.order.dto.PlaceOrderItemRequest;
import tech.goticket.backendapi.order.dto.PlaceOrderRequest;
import tech.goticket.backendapi.order.enums.OrderStatus;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.order.repository.OrderStatusRepository;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.enums.EligibilityType;
import tech.goticket.backendapi.ticket.enums.TicketType;
import tech.goticket.backendapi.ticket.repository.EligibilityTypeRepository;
import tech.goticket.backendapi.ticket.repository.TicketTypeRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPersistenceService {
    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final EventDateRepository eventDateRepository;
    private final ClientRepository clientRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final EligibilityTypeRepository eligibilityTypeRepository;
    private final ReservationService reservationService;
    private final FeeCalculator feeCalculator;

    @Value("${goticket.checkout.reservation.ttl-minutes:10}")
    private long reservationTtlMinutes;

    @Transactional
    public Order executePlaceOrder(PlaceOrderRequest request, UUID buyerId, String idempotencyKey) {
        Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Idempotency hit: returning existing order {}", existing.get().getOrderId());
            return existing.get();
        }

        EventDate eventDate = eventDateRepository.findById(request.eventDateId())
                .orElseThrow(() -> new ResourceNotFoundException("EventDate não encontrada: " + request.eventDateId()));

        Client buyer = clientRepository.findByUserId(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente autenticado não encontrado: " + buyerId));

        validateEventCanReceiveSales(eventDate);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal feesTotal = BigDecimal.ZERO;

        // Agrupa quantidade por allotment ANTES de reservar
        Map<Long, Integer> qtyByAllotment = new HashMap<>();
        for (PlaceOrderItemRequest item : request.items()) {
            qtyByAllotment.merge(item.batchAllotmentId(), 1, Integer::sum);
        }

        Map<Long, BatchAllotment> reservedAllotments = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : qtyByAllotment.entrySet()) {
            BatchAllotment reserved = reservationService.reserveOrThrow(entry.getKey(), entry.getValue());
            reservedAllotments.put(entry.getKey(), reserved);
        }

        OrderStatus pendingStatus = orderStatusRepository.findByName(OrderStatus.Values.PENDING_PAYMENT.name())
                .orElseThrow(() -> new IllegalStateException("Seed OrderStatus PENDING_PAYMENT ausente."));

        Instant now = Instant.now();

        Order order = new Order();
        order.setIdempotencyKey(idempotencyKey);
        order.setBuyer(buyer);
        order.setEvent(eventDate.getEvent());
        order.setEventDate(eventDate);
        order.setStatus(pendingStatus);
        order.setCurrency("BRL");
        order.setPaymentProvider("STRIPE");
        order.setPlacedAt(now);
        order.setExpiresAt(now.plus(Duration.ofMinutes(reservationTtlMinutes)));

        Long organizerId = eventDate.getEvent().getOrganizer() != null
                ? null  // organizer.userId é UUID; deixa null se FeeCalculator espera Long
                : null;
        // OBS: O FeeCalculator espera Long para organizerId. Como Organizer.userId é UUID,
        // o scope ORGANIZER da Fee precisaria de ajuste de modelagem. No D2 simplifico:
        // só PLATFORM funciona; ORGANIZER/EVENT ficam para evolução.

        for(PlaceOrderItemRequest itemRequest : request.items()) {
            BatchAllotment allotment = reservedAllotments.get(itemRequest.batchAllotmentId());

            Long allotmentEventDateId = allotment.getBatch().getEventDateSector().getEventDate().getEventDateId();
            if (!allotmentEventDateId.equals(request.eventDateId())) {
                throw new InvalidArgumentException(String.format(
                        "Allotment %d pertence à EventDate %d (evento '%s'), não à EventDate %d que você enviou.",
                        itemRequest.batchAllotmentId(),
                        allotmentEventDateId,
                        allotment.getBatch().getEventDateSector().getEventDate().getEvent().getTitle(),
                        request.eventDateId()
                ));
            }

            TicketType ticketType = ticketTypeRepository.findById(itemRequest.ticketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketType não encontrado: " + itemRequest.ticketTypeId()));

            if (!allotment.getTicketType().getTicketTypeId().equals(ticketType.getTicketTypeId())) {
                throw new InvalidArgumentException("TicketType " + ticketType.getName() +
                                " não corresponde ao allotment " + itemRequest.batchAllotmentId() +
                                " (que é " + allotment.getTicketType().getName() + ")");
            }

            EligibilityType eligibility = null;
            if (ticketType.isHalf()) {
                if (itemRequest.eligibilityTypeId() == null) {
                    throw new InvalidArgumentException("Meia-entrada exige eligibilityTypeId.");
                }
                eligibility = eligibilityTypeRepository.findById(itemRequest.eligibilityTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "EligibilityType não encontrado: " + itemRequest.eligibilityTypeId()));
                if (eligibility.getName().equals(EligibilityType.Values.STUDENT.name()) && itemRequest.eligibilityDocumentNumber() == null) {
                    throw new InvalidArgumentException("Meia-entrada de estudante exige eligibilityTypeDocumentNumber.");
                }
            }

            BigDecimal unitPrice = allotment.effectivePrice();
            FeeBreakdown breakdown = feeCalculator.compute(unitPrice, eventDate.getEvent().getEventId(), null);
            BigDecimal feeAmount = breakdown.feesTotal();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setBatchAllotment(allotment);
            oi.setTicketType(ticketType);
            oi.setHolderName(itemRequest.holderName());
            oi.setHolderDocument(itemRequest.holderDocument());
            oi.setEligibilityType(eligibility);
            oi.setEligibilityDocumentNumber(itemRequest.eligibilityDocumentNumber());
            oi.setUnitPrice(unitPrice);
            oi.setFeeAmount(feeAmount);

            order.getItems().add(oi);

            subtotal = subtotal.add(unitPrice);
            feesTotal = feesTotal.add(feeAmount);
        }

        order.setSubtotal(subtotal);
        order.setFeesTotal(feesTotal);
        order.setTotalPrice(subtotal.add(feesTotal));

        Order saved = orderRepository.save(order);

        log.info("Order {} criada (PENDING_PAYMENT) para buyer {}, total {} {}, expira em {}",
                saved.getOrderId(), buyerId, saved.getTotalPrice(), saved.getCurrency(),
                saved.getExpiresAt());

        return saved;
    }

    private void validateEventCanReceiveSales(EventDate eventDate) {
        var event = eventDate.getEvent();

        if (!event.getStatus().isApproved()) {
            throw new ForbiddenActionException(
                    "Evento não está aprovado para vendas. Status: " + event.getStatus().getName());
        }

        LocalDateTime now = LocalDateTime.now();
        if (event.getSalesStartDate() != null && now.isBefore(event.getSalesStartDate())) {
            throw new ForbiddenActionException(
                    "Vendas para este evento ainda não começaram. Início: " + event.getSalesStartDate());
        }

        if (now.isAfter(eventDate.getStartDate())) {
            throw new ForbiddenActionException(
                    "Sessão já iniciada/encerrada. Início: " + eventDate.getStartDate());
        }
    }
}
