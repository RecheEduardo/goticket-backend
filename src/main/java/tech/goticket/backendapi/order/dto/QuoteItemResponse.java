package tech.goticket.backendapi.order.dto;

import jakarta.validation.constraints.NotNull;
import tech.goticket.backendapi.fee.dto.AppliedFeeDTO;

import java.math.BigDecimal;
import java.util.List;

public record QuoteItemResponse(
        Long batchAllotmentId,
        String ticketTypeName,
        BigDecimal unitPrice,
        List<AppliedFeeDTO> appliedFees,
        BigDecimal feesTotal,
        BigDecimal totalWithFees,
        Integer availableTickets
) {
}
