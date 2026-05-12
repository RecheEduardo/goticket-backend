package tech.goticket.backendapi.order.dto;

import tech.goticket.backendapi.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String status,
        Long eventId,
        Long eventDateId,
        BigDecimal subtotal,
        BigDecimal feesTotal,
        BigDecimal totalPrice,
        String currency,
        String paymentIntentId,
        Instant placedAt,
        Instant expiresAt,
        Instant paidAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getOrderId(),
                o.getStatus().getName(),
                o.getEvent().getEventId(),
                o.getEventDate().getEventDateId(),
                o.getSubtotal(),
                o.getFeesTotal(),
                o.getTotalPrice(),
                o.getCurrency(),
                o.getPaymentIntentId(),
                o.getPlacedAt(),
                o.getExpiresAt(),
                o.getPaidAt(),
                o.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}