package tech.goticket.backendapi.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderSummaryItemDTO (
        Long orderItemId,
        String sectorName,
        String ticketTypeName,
        BigDecimal unitPrice,
        BigDecimal feeAmount,
        BigDecimal itemTotal,
        String holderName,
        UUID ticketId,
        String qrToken
) {}
