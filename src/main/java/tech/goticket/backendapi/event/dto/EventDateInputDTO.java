package tech.goticket.backendapi.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventDateInputDTO(
        @NotNull(message = "A data de início é obrigatória.")
        @Future(message = "A data de início deve ser uma data futura.")
        LocalDateTime startDate,

        @NotNull(message = "A data de término é obrigatória.")
        @Future(message = "A data de término deve ser uma data futura.")
        LocalDateTime endDate
) {}