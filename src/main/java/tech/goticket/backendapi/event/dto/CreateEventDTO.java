package tech.goticket.backendapi.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateEventDTO(
        @NotBlank(message = "O título do evento é um campo obrigatório.")
        String title,

        @NotBlank(message = "A descrição do evento é um campo obrigatório.")
        String description,

        @NotNull(message = "O ID da categoria é um campo obrigatório.")
        Long categoryId,

        @NotNull(message = "O ID do espaço é um campo obrigatório.")
        Long venueId,

        @NotNull(message = "A restrição de idade é um campo obrigatório.")
        Integer ageRestriction,

        LocalDateTime salesStartDate,

        @NotEmpty(message = "Pelo menos uma data deve ser informada para o evento.")
        @Valid
        List<EventDateInputDTO> eventDates,

        UUID organizerId
    ) {}
