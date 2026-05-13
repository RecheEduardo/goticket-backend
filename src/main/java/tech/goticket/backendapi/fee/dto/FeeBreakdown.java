package tech.goticket.backendapi.fee.dto;

import java.math.BigDecimal;
import java.util.List;

public record FeeBreakdown(
        BigDecimal unitPrice,
        List<AppliedFeeDTO> appliedFees,
        BigDecimal feesTotal,
        BigDecimal totalWithFees
) { }