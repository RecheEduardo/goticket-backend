package tech.goticket.backendapi.fee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.fee.Fee;
import tech.goticket.backendapi.fee.FeeRepository;
import tech.goticket.backendapi.fee.dto.AppliedFeeDTO;
import tech.goticket.backendapi.fee.dto.FeeBreakdown;
import tech.goticket.backendapi.fee.enums.FeeAppliesTo;
import tech.goticket.backendapi.fee.enums.FeeScope;
import tech.goticket.backendapi.fee.enums.FeeType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeeCalculator {
    private final FeeRepository feeRepository;

    public FeeBreakdown compute(BigDecimal unitPrice, Long eventId, Long organizerId) {
        Instant now = Instant.now();
        List<AppliedFeeDTO> applied = new ArrayList<>();
        BigDecimal feesTotal = BigDecimal.ZERO;

        for (Fee fee : feeRepository.findActiveByScope(FeeScope.PLATFORM, null, now)) {
            if (fee.getAppliesTo() != FeeAppliesTo.PER_TICKET) continue;
            BigDecimal amount = applyFee(unitPrice, fee);
            applied.add(AppliedFeeDTO.of(fee, amount));
            feesTotal = feesTotal.add(amount);
        }

        if (organizerId != null) {
            for (Fee fee : feeRepository.findActiveByScope(FeeScope.ORGANIZER, organizerId, now)) {
                if (fee.getAppliesTo() != FeeAppliesTo.PER_TICKET) continue;
                BigDecimal amount = applyFee(unitPrice, fee);
                applied.add(AppliedFeeDTO.of(fee, amount));
                feesTotal = feesTotal.add(amount);
            }
        }

        if (eventId != null) {
            for (Fee fee : feeRepository.findActiveByScope(FeeScope.EVENT, eventId, now)) {
                if (fee.getAppliesTo() != FeeAppliesTo.PER_TICKET) continue;
                BigDecimal amount = applyFee(unitPrice, fee);
                applied.add(AppliedFeeDTO.of(fee, amount));
                feesTotal = feesTotal.add(amount);
            }
        }

        return new FeeBreakdown(
                unitPrice,
                applied,
                feesTotal,
                unitPrice.add(feesTotal)
        );
    }

    private BigDecimal applyFee(BigDecimal base, Fee fee) {
        BigDecimal amount = (fee.getFeeType() == FeeType.PERCENT)
                ? base.multiply(fee.getValue())
                : fee.getValue();
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
