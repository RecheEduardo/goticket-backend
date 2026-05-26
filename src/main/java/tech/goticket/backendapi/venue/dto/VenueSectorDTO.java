package tech.goticket.backendapi.venue.dto;

import tech.goticket.backendapi.venue.VenueSector;

public record VenueSectorDTO(
        Long sectorId,
        String name,
        String description,
        Integer maxCapacity,
        String mapElementId
) {
    public VenueSectorDTO(VenueSector sector) {
        this(
                sector.getSectorId(),
                sector.getName(),
                sector.getDescription(),
                sector.getMaxCapacity(),
                sector.getMapElementId()
        );
    }
}
