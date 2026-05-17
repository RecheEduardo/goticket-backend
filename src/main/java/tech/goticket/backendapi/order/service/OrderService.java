package tech.goticket.backendapi.order.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.repository.EventDateRepository;
import tech.goticket.backendapi.fee.dto.FeeBreakdown;
import tech.goticket.backendapi.fee.service.FeeCalculator;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.dto.*;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.payment.service.StripeService;
import tech.goticket.backendapi.shared.exception.ConflictException;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ReservationContentionException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.payment.StripeIntegrationException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.enums.TicketType;
import tech.goticket.backendapi.ticket.repository.BatchAllotmentRepository;
import tech.goticket.backendapi.ticket.repository.TicketTypeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private static final int MAX_RESERVATION_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 50L;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    private final OrderPersistenceService persistenceService;
    private final OrderRepository orderRepository;
    private final EventDateRepository eventDateRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final BatchAllotmentRepository batchAllotmentRepository;
    private final FeeCalculator feeCalculator;
    private final StripeService stripeService;

    public PlaceOrderResponse placeOrder(PlaceOrderRequest request, UUID buyerId, String idempotencyKey) {
        validateIdempotencyKey(idempotencyKey);

        Order order = createOrderWithReservation(request, buyerId, idempotencyKey);
        if (order.getPaymentIntentId() != null) {
            return PlaceOrderResponse.from(
                    order,
                    fetchClientSecret(order.getPaymentIntentId()),
                    stripePublishableKey
            );
        }

        PaymentIntent intent;
        try {
            intent = stripeService.createPaymentIntent(order);
        } catch (StripeIntegrationException e) {
            throw e;
        }

        Order updated = persistenceService.attachPaymentIntent(order.getOrderId(), intent.getId());

        return PlaceOrderResponse.from(updated, intent.getClientSecret(), stripePublishableKey);
    }

    private Order createOrderWithReservation(PlaceOrderRequest request, UUID buyerId, String idempotencyKey) {
        for (int attempt = 0; attempt < MAX_RESERVATION_ATTEMPTS; attempt++) {
            try {
                return persistenceService.executePlaceOrder(request, buyerId, idempotencyKey);
            }
            catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock conflict on attempt {}/{}: {}",
                        attempt + 1, MAX_RESERVATION_ATTEMPTS, e.getMessage());
                sleepWithBackoff(attempt);
            }
            catch (DataIntegrityViolationException e) {
                // UNIQUE(idempotency_key) explodiu: outra request com mesma key
                // ganhou a corrida e gravou primeiro. Recupera a Order dela.
                if (isIdempotencyKeyConflict(e)) {
                    return orderRepository.findByIdempotencyKey(idempotencyKey)
                            .orElseThrow(() -> new ConflictException(
                                    "Conflito de idempotency-key, mas Order não encontrada na recovery."));
                }
                throw e;
            }
        }
        throw new ReservationContentionException(
                "Não foi possível reservar os ingressos após " + MAX_RESERVATION_ATTEMPTS + ". Tente de novo em alguns segundos.");
    }

    private String fetchClientSecret(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId).getClientSecret();
        } catch (StripeException e) {
            throw new StripeIntegrationException(
                    "Falha ao recuperar PaymentIntent existente: " + paymentIntentId, e);
        }
    }

    private void validateIdempotencyKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ConflictException("Header 'Idempotency-Key' é obrigatório.");
        }
        if (key.length() > 80) {
            throw new ConflictException("Idempotency-Key não pode ter mais de 80 caracteres.");
        }
    }

    private void sleepWithBackoff(int attempt) {
        try {
            Thread.sleep(INITIAL_BACKOFF_MS * (1L << attempt));  // 50ms, 100ms, 200ms
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ReservationContentionException("Interrompido durante retentativa.");
        }
    }

    private boolean isIdempotencyKeyConflict(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        return cause.getMessage() != null && cause.getMessage().toLowerCase().contains("uq_orders_idempotency_key");
    }

    public Order getById(Long orderId, UUID requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada: " + orderId));

        if (!order.getBuyer().getUserId().equals(requesterId)) {
            throw new ResourceNotFoundException("Order não encontrada: " + orderId);
        }
        return order;
    }

    public QuoteResponse quote(QuoteRequest request) {
        EventDate eventDate = eventDateRepository.findById(request.eventDateId())
                .orElseThrow(() -> new ResourceNotFoundException("EventDate não encontrada: " + request.eventDateId()));

        Long eventId = eventDate.getEvent().getEventId();
        String currency = "BRL";

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal feesTotal = BigDecimal.ZERO;
        List<QuoteItemResponse> itemResponses = new ArrayList<>();

        for (QuoteItemRequest itemReq : request.items()) {
            BatchAllotment allotment = batchAllotmentRepository.findById(itemReq.batchAllotmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Allotment não encontrado: " + itemReq.batchAllotmentId()));

            TicketType ticketType = ticketTypeRepository.findById(itemReq.ticketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketType não encontrado: " + itemReq.ticketTypeId()));

            if (!allotment.getTicketType().getTicketTypeId().equals(ticketType.getTicketTypeId())) {
                throw new InvalidArgumentException(
                        "TicketType " + ticketType.getName() +
                        " não corresponde ao allotment " + itemReq.batchAllotmentId());
            }

            Long allotmentEventDateId = allotment.getBatch().getEventDateSector().getEventDate().getEventDateId();
            if (!allotmentEventDateId.equals(request.eventDateId())) {
                throw new InvalidArgumentException(String.format(
                        "Allotment %d pertence à EventDate %d (evento '%s'), não à EventDate %d que você enviou.",
                        itemReq.batchAllotmentId(),
                        allotmentEventDateId,
                        allotment.getBatch().getEventDateSector().getEventDate().getEvent().getTitle(),
                        request.eventDateId()
                ));
            }

            BigDecimal unitPrice = allotment.effectivePrice();
            FeeBreakdown breakdown = feeCalculator.compute(unitPrice, eventId, null);

            itemResponses.add(new QuoteItemResponse(
                    allotment.getAllotmentId(),
                    ticketType.getName(),
                    unitPrice,
                    breakdown.appliedFees(),
                    breakdown.feesTotal(),
                    breakdown.totalWithFees(),
                    allotment.getAvailableTickets()
            ));

            subtotal = subtotal.add(unitPrice);
            feesTotal = feesTotal.add(breakdown.feesTotal());
        }

        return new QuoteResponse(
                eventId,
                eventDate.getEvent().getTitle(),
                eventDate.getEventDateId(),
                currency,
                itemResponses,
                subtotal,
                feesTotal,
                subtotal.add(feesTotal)
        );
    }
}
