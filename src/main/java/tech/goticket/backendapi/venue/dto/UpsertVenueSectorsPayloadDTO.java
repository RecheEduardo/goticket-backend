package tech.goticket.backendapi.venue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpsertVenueSectorsPayloadDTO(
        @NotNull(message = "A lista de setores é obrigatória.")
        @Valid
        List<UpsertVenueSectorDTO> sectors
) {}
