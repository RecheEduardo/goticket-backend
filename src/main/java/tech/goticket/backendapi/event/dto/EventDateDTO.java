package tech.goticket.backendapi.event.dto;

import tech.goticket.backendapi.event.EventDate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record EventDateDTO(
        Long eventDateID,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long statusId,
        List<EventPageDTO.EventDateSectorDTO> dateSectors
) {
    public EventDateDTO(EventDate ed) {
        this(
                ed.getEventDateId(),
                ed.getStartDate(),
                ed.getEndDate(),
                ed.getStatus() != null ? ed.getStatus().getStatusId() : null,
                ed.getDateSectors().stream()
                        .sorted(Comparator.comparing(eds -> eds.getEventSector().getName()))
                        .map(EventPageDTO.EventDateSectorDTO::new)
                        .toList()
        );
    }
}