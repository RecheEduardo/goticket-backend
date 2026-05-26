package tech.goticket.backendapi.order.dto;

public record OrderStatusDTO(
        Long orderId,
        String status
) {
}
