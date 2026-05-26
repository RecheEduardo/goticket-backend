package tech.goticket.backendapi.order.service;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.OrderItem;
import tech.goticket.backendapi.order.enums.OrderStatus;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.order.repository.OrderStatusRepository;
import tech.goticket.backendapi.ticket.Ticket;
import tech.goticket.backendapi.ticket.service.TicketGenerationService;

import java.time.Instant;
import java.util.Map;

import static tech.goticket.backendapi.order.util.OrderUtils.countByAllotment;

@Service
@RequiredArgsConstructor
public class OrderPaymentService {

    private static final Logger log = LoggerFactory.getLogger(OrderPaymentService.class);

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final TicketGenerationService ticketGenerationService;
    private final ReservationService reservationService;

    @Transactional
    public void markPaidByIntent(PaymentIntent intent) {
        Order order = orderRepository.findByPaymentIntentId(intent.getId())
                .orElse(null);

        if (order == null) {
            log.error("PaymentIntent {} succeeded mas Order não foi encontrada. Refund automático.", intent.getId());
            refundOnStripe(intent.getId(), "Order não encontrada na GoTicket");
            return;
        }

        if(order.getStatus().isPaid()) {
            log.info("Order {} já está PAID — ignorando evento duplicado.", order.getOrderId());
            return;
        }

        if (order.getStatus().isExpired() || order.getStatus().isCanceled()) {
            log.warn("PaymentIntent {} succeeded mas Order {} está {}. Refund automático.",
                    intent.getId(), order.getOrderId(), order.getStatus().getName());
            refundOnStripe(intent.getId(), "Reserva expirada no GoTicket");
            return;
        }

        if (!order.getStatus().isPendingPayment()) {
            log.error("Estado inesperado da Order {}: {}",
                    order.getOrderId(), order.getStatus().getName());
            return;
        }

        OrderStatus paidStatus = findStatus(OrderStatus.Values.PAID);
        order.markPaid(paidStatus, Instant.now());
        order.setPaymentMethodSnapshot(safePaymentMethodId(intent));

        for (OrderItem item : order.getItems()) {
            Ticket ticket = ticketGenerationService.generateTicket(item, order.getBuyer());
            item.setTicket(ticket);
        }

        Map<Long, Integer> itemsByAllotment = countByAllotment(order);
        for (Map.Entry<Long, Integer> e : itemsByAllotment.entrySet()) {
            reservationService.confirmSale(e.getKey(), e.getValue());
        }

        orderRepository.save(order);
        log.info("Order {} marcada como PAID. {} tickets gerados.",
                order.getOrderId(), order.getItems().size());
    }

    @Transactional
    public void cancelByIntent(PaymentIntent intent, String reason) {
        Order order = orderRepository.findByPaymentIntentId(intent.getId())
                .orElse(null);

        if (order == null) {
            log.warn("PaymentIntent {} {} mas Order não encontrada — ignorando.",
                    intent.getId(), reason);
            return;
        }

        if (!order.getStatus().isPendingPayment()) {
            log.info("Order {} já está em {} — ignorando {} da Stripe.",
                    order.getOrderId(), order.getStatus().getName(), reason);
            return;
        }

        OrderStatus canceledStatus = findStatus(OrderStatus.Values.CANCELED);
        order.markCanceled(canceledStatus, "Stripe: " + reason);

        Map<Long, Integer> itemsByAllotment = countByAllotment(order);
        for (Map.Entry<Long, Integer> e : itemsByAllotment.entrySet()) {
            reservationService.releaseReservation(e.getKey(), e.getValue());
        }

        orderRepository.save(order);
        log.info("Order {} cancelada via webhook ({}).", order.getOrderId(), reason);
    }

    @Transactional
    public void markRefundedByIntent(Event event) {
        String paymentIntentId = extractPaymentIntentFromChargeEvent(event);
        Order order = orderRepository.findByPaymentIntentId(paymentIntentId).orElse(null);

        if (order == null || !order.getStatus().isPaid()) {
            log.warn("Refund recebido para PI {} mas Order não está PAID — ignorando.",
                    paymentIntentId);
            return;
        }

        OrderStatus refundedStatus = findStatus(OrderStatus.Values.REFUNDED);
        order.markRefunded(refundedStatus, Instant.now());

        for (OrderItem item : order.getItems()) {
            if (item.getTicket() != null) {
                ticketGenerationService.markRefunded(item.getTicket());
            }
        }

        orderRepository.save(order);
        log.info("Order {} marcada como REFUNDED.", order.getOrderId());
    }

    private void refundOnStripe(String paymentIntentId, String reason) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .putMetadata("auto_refund_reason", reason)
                    .build();
            Refund.create(params);
            log.info("Refund automático emitido para PI {}: {}", paymentIntentId, reason);
        } catch (StripeException e) {
            log.error("FALHA ao emitir refund automático para PI {}: {}",
                    paymentIntentId, e.getMessage(), e);
        }
    }

    private OrderStatus findStatus(OrderStatus.Values value) {
        return orderStatusRepository.findByName(value.name())
                .orElseThrow(() -> new IllegalStateException(
                        "OrderStatus " + value.name() + " não encontrado."));
    }

    private String safePaymentMethodId(PaymentIntent intent) {
        try { return intent.getPaymentMethod(); }
        catch (Exception e) { return null; }
    }

    private String extractPaymentIntentFromChargeEvent(Event event) {
        EventDataObjectDeserializer d = event.getDataObjectDeserializer();
        StripeObject obj = d.getObject().orElseGet(() -> {
            try { return d.deserializeUnsafe(); }
            catch (Exception e) {
                throw new IllegalStateException(
                        "Falha ao desserializar Charge do evento " + event.getId(), e);
            }
        });

        if (obj instanceof Charge charge) {
            return charge.getPaymentIntent();
        }
        throw new IllegalStateException(
                "Esperava Charge no evento " + event.getId() +
                        ", recebido: " + obj.getClass().getSimpleName());
    }
}
