package tech.goticket.backendapi.ticket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateBatchAllotmentDTO(
        @NotNull Long ticketTypeId,
        @NotNull @Positive Integer quota,
        BigDecimal price
) {
}
