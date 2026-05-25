package tech.goticket.backendapi.event.dto;

public record UpdateEventSectorDTO(
        String name,
        String description,
        Boolean hasNumberedSeats
) {}
