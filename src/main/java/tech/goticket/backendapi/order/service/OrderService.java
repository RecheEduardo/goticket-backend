package tech.goticket.backendapi.order.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.repository.EventDateRepository;
import tech.goticket.backendapi.event.repository.EventImageRepository;
import tech.goticket.backendapi.fee.dto.FeeBreakdown;
import tech.goticket.backendapi.fee.service.FeeCalculator;
import tech.goticket.backendapi.idempotency.service.IdempotencyService;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.OrderItem;
import tech.goticket.backendapi.order.dto.*;
import tech.goticket.backendapi.order.enums.OrderStatus;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.order.repository.OrderStatusRepository;
import tech.goticket.backendapi.payment.service.StripeService;
import tech.goticket.backendapi.shared.exception.*;
import tech.goticket.backendapi.shared.exception.payment.StripeIntegrationException;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.enums.TicketType;
import tech.goticket.backendapi.ticket.repository.BatchAllotmentRepository;
import tech.goticket.backendapi.ticket.repository.TicketTypeRepository;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.*;

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
    private final OrderStatusRepository orderStatusRepository;
    private final ReservationService reservationService;
    private final IdempotencyService idempotencyService;
    private final EventDateRepository eventDateRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final BatchAllotmentRepository batchAllotmentRepository;
    private final UserRepository userRepository;
    private final FeeCalculator feeCalculator;
    private final StripeService stripeService;
    private final EventImageRepository eventImageRepository;

    public PlaceOrderResponse placeOrder(PlaceOrderRequest request, UUID buyerId, String idempotencyKey, String rawBodyJson) {
        var lookup = idempotencyService.checkAndRegister(idempotencyKey, buyerId, "POST /orders", rawBodyJson);

        if (lookup instanceof IdempotencyService.Replay replay) {
            Order existing = orderRepository.findById(replay.orderId())
                    .orElseThrow(() -> new IllegalStateException("Idempotency-Key aponta para orderId não existente."));
            String clientSecret = (existing.getPaymentIntentId() != null)
                    ? fetchClientSecret(existing.getPaymentIntentId())
                    : null;
            return PlaceOrderResponse.from(existing, clientSecret, stripePublishableKey);
        }

        Order order = createOrderWithReservation(request, buyerId, idempotencyKey);
        PaymentIntent intent = stripeService.createPaymentIntent(order, idempotencyKey);
        Order updated = persistenceService.attachPaymentIntent(order.getOrderId(), intent.getId());

        idempotencyService.linkToOrder(idempotencyKey, updated.getOrderId(), 201);

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

    @Transactional(readOnly = true)
    public Order getById(Long orderId, UUID requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada: " + orderId));

        boolean isOwner = order.getBuyer().getUserId().equals(requesterId);
        User isAdminCheck = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não existe na base de dados."));
        boolean isAdmin = isAdminCheck.getRole().getRoleId() == Role.Values.ADMIN.getRoleId();

        if (!isAdmin && !isOwner) {
            throw new ResourceNotFoundException("Order não encontrada: " + orderId);
        }
        return order;
    }

    @Transactional(readOnly = true)
    public OrderStatusDTO getStatus(Long orderId, UUID requesterId) {
        return orderRepository.findStatusByIdAndBuyer(orderId, requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada: " + orderId));
    }

    @Transactional(readOnly = true)
    public MyOrderListDTO listMyOrders(UUID buyerId, Pageable pageable) {
        var orders = orderRepository.findMyOrders(buyerId, pageable);

        return new MyOrderListDTO(orders.getNumber(),
                                  orders.getSize(),
                                  orders.getTotalPages(),
                                  orders.getTotalElements(),
                                  orders.getContent());
    }

    @Transactional(readOnly = true)
    public OrderSummaryResponse getSummary(Long orderId, UUID requesterId) {
        Order order = orderRepository.findByIdWithFullGraph(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order não encontrada: " + orderId));

        boolean isOwner = order.getBuyer().getUserId().equals(requesterId);
        boolean isAdmin = userRepository.findById(requesterId)
                .map(u -> u.getRole().getRoleId() == Role.Values.ADMIN.getRoleId())
                .orElse(false);
        if (!isOwner && !isAdmin) {
            throw new ResourceNotFoundException("Order não encontrada: " + orderId);
        }

        String imageKey = eventImageRepository.findMainImageKey(order.getEvent().getEventId())
                .orElse(null);

        List<OrderSummaryItemDTO> items = order.getItems().stream()
                .map(it -> new OrderSummaryItemDTO(
                        it.getOrderItemId(),
                        it.getBatchAllotment().getBatch().getEventDateSector()
                                .getEventSector().getName(),
                        it.getTicketType().getName(),
                        it.getUnitPrice(),
                        it.getFeeAmount(),
                        it.getUnitPrice().add(it.getFeeAmount()),
                        it.getHolderName(),
                        it.getTicket() != null ? it.getTicket().getTicketId() : null,
                        it.getTicket() != null ? it.getTicket().getQrToken() : null
                ))
                .toList();

        return new OrderSummaryResponse(
                order.getOrderId(),
                order.getStatus().getName(),
                order.getEvent().getEventId(),
                order.getEvent().getTitle(),
                imageKey,
                order.getEventDate().getStartDate(),
                order.getEvent().getVenue().getName(),
                order.getEvent().getVenue().getCity(),
                order.getSubtotal(),
                order.getFeesTotal(),
                order.getTotalPrice(),
                order.getCurrency(),
                order.getPaidAt(),
                items
        );
    }

    @Transactional
    public Order cancelByBuyer(Long orderId, UUID requesterId, String reason) {
        Order order = getById(orderId, requesterId);

        if(!order.canCancel()) {
            throw new ForbiddenActionException("Order com status: " + order.getStatus().getName() + " não pode ser cancelada.");
        }

        OrderStatus canceledStatus = orderStatusRepository.findByName(OrderStatus.Values.CANCELED.name())
                .orElseThrow(() -> new IllegalStateException("OrderStatus CANCELED não existente."));

        order.markCanceled(canceledStatus, reason);

        Map<Long, Integer> byAllotment = countByAllotment(order);
        for (Map.Entry<Long, Integer> e : byAllotment.entrySet()) {
            reservationService.releaseReservation(e.getKey(), e.getValue());
        }

        Order saved = orderRepository.save(order);

        if (saved.getPaymentIntentId() != null) {
            stripeService.tryCancelPaymentIntent(saved.getPaymentIntentId(), reason);
        }

        return saved;
    }

    private Map<Long, Integer> countByAllotment(Order order) {
        Map<Long, Integer> counts = new HashMap<>();
        for (OrderItem item : order.getItems()) {
            counts.merge(item.getBatchAllotment().getAllotmentId(), 1, Integer::sum);
        }
        return counts;
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
