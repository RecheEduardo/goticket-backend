package tech.goticket.backendapi.ticket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateTicketBatchDTO(
        @NotNull @Positive BigDecimal price,
        LocalDateTime activationDate,
        @NotEmpty @Valid List<CreateBatchAllotmentDTO> allotments
) {
}
