package tech.goticket.backendapi.fee.dto;

import tech.goticket.backendapi.fee.Fee;
import tech.goticket.backendapi.fee.enums.FeeScope;
import tech.goticket.backendapi.fee.enums.FeeType;

import java.math.BigDecimal;

public record AppliedFeeDTO(
        Long feeId,
        String name,
        String description,
        FeeType feeType,
        BigDecimal feeValue,
        FeeScope scope,
        BigDecimal computedAmount
) {
    public static AppliedFeeDTO of(Fee fee, BigDecimal computedAmount) {
        return new AppliedFeeDTO(
                fee.getFeeId(),
                fee.getName(),
                fee.getDescription(),
                fee.getFeeType(),
                fee.getValue(),
                fee.getScope(),
                computedAmount
        );
    }
}
