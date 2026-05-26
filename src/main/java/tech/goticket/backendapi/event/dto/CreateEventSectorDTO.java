package tech.goticket.backendapi.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventSectorDTO(
        @NotBlank(message = "name é obrigatório.") String name,
        @NotBlank(message = "description é obrigatório.") String description,
        @NotNull(message = "hasNumberedSeats é obrigatório.") Boolean hasNumberedSeats,
        @NotNull(message = "venueSectorId é obrigatório.") Long venueSectorId
) {}
