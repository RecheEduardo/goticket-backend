package tech.goticket.backendapi.order.dto;

import tech.goticket.backendapi.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PlaceOrderResponse(
        Long orderId,
        String status,
        BigDecimal totalPrice,
        String currency,
        Instant expiresAt,
        String paymentIntentId,
        String clientSecret,
        String publishableKey,
        List<OrderItemResponse> items
) {
    public static PlaceOrderResponse from(Order o, String clientSecret, String publishableKey) {
        return new PlaceOrderResponse(
                o.getOrderId(),
                o.getStatus().getName(),
                o.getTotalPrice(),
                o.getCurrency(),
                o.getExpiresAt(),
                o.getPaymentIntentId(),
                clientSecret,
                publishableKey,
                o.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
