package tech.goticket.backendapi.order.dto;

import tech.goticket.backendapi.order.Order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderListItemDTO(
        Long orderId,
        String status,
        String eventTitle,
        BigDecimal totalPrice,
        Instant placedAt
) {
    public static OrderListItemDTO from(Order o) {
        return new OrderListItemDTO(
                o.getOrderId(),
                o.getStatus().getName(),
                o.getEvent().getTitle(),
                o.getTotalPrice(),
                o.getPlacedAt()
        );
    }
}