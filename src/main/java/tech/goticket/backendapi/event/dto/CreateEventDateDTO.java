package tech.goticket.backendapi.event.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEventDateDTO(
        @NotNull(message = "startDate é obrigatório.") LocalDateTime startDate,
        @NotNull(message = "endDate é obrigatório.") LocalDateTime endDate
) {}