package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.event.EventSector;
import tech.goticket.backendapi.ticket.TicketBatch;

import java.util.Optional;

@Getter
@Setter
public class EventSectorPublicDTO {

    public EventSectorPublicDTO () {}

    public EventSectorPublicDTO (EventSector eventSector) {
        this.name = eventSector.getName();
        this.description = eventSector.getDescription();
        this.currentBatch = eventSector.getCurrentBatch();
        this.totalTickets = eventSector.getTotalTickets();
        this.availableTickets = eventSector.getAvailableTickets();
    }

    private String name;

    private String description;

    private Optional<TicketBatch> currentBatch;

    private Integer totalTickets;

    private Integer availableTickets;
}
