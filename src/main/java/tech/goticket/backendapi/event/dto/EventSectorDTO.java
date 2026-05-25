package tech.goticket.backendapi.event.dto;

import tech.goticket.backendapi.event.EventSector;

public record EventSectorDTO(
        Long sectorId,
        String name,
        String description,
        boolean hasNumberedSeats,
        Long venueSectorId,
        String mapElementId,
        Integer venueSectorMaxCapacity
) {
    public EventSectorDTO(EventSector s) {
        this(
                s.getSectorId(),
                s.getName(),
                s.getDescription(),
                s.isHasNumberedSeats(),
                s.getVenueSectorId(),
                s.getMapElementId(),
                s.getVenueSector() != null ? s.getVenueSector().getMaxCapacity() : null
        );
    }
}
