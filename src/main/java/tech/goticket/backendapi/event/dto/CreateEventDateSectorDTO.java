package tech.goticket.backendapi.event.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEventDateSectorDTO(@NotNull Long eventSectorId) {
}
