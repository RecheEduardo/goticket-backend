package tech.goticket.backendapi.order.dto;

import tech.goticket.backendapi.order.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        Long orderItemId,
        Long batchAllotmentId,
        String ticketTypeName,
        String holderName,
        String holderDocument,
        String eligibilityTypeName,
        BigDecimal unitPrice,
        BigDecimal feeAmount,
        UUID ticketId
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getOrderItemId(),
                item.getBatchAllotment().getAllotmentId(),
                item.getTicketType().getName(),
                item.getHolderName(),
                item.getHolderDocument(),
                item.getEligibilityType() != null ? item.getEligibilityType().getName() : null,
                item.getUnitPrice(),
                item.getFeeAmount(),
                item.getTicket() != null ? item.getTicket().getTicketId() : null
        );
    }
}