package tech.goticket.backendapi.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpsertVenueSectorDTO(
        Long sectorId,
        @NotBlank(message = "O nome do setor é obrigatório.")
        String name,
        @NotBlank(message = "A descrição do setor é obrigatória.")
        String description,
        @NotNull(message = "A capacidade máxima do setor é obrigatória.")
        Integer maxCapacity,
        String mapElementId
) {}
