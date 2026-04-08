package tech.goticket.backendapi.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventDTO(
        @NotBlank(message = "O título do evento é um campo obrigatório.")
        String title,

        @NotBlank(message = "A descrição do evento é um campo obrigatório.")
        String description,

        @NotNull(message = "A restrição de idade é um campo obrigatório.")
        Integer ageRestriction,

        @NotNull(message = "A data de início é um campo obrigatório.")
        @Future(message = "A data de início deve ser uma data futura.")
        LocalDateTime startDate,

        @NotNull(message = "A data de término é um campo obrigatório.")
        @Future(message = "A data de término deve ser uma data futura.")
        LocalDateTime endDate,

        LocalDateTime salesStartDate,

        UUID organizerID
    ) {}
