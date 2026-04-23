package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.sector.Sector;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.venue.VenueSector;

import java.util.List;

@Entity
@Table(name = "tb_event_sectors")
@Getter
@Setter
public class EventSector extends Sector {

    @Column(name = "has_numbered_seats", nullable = false)
    private boolean hasNumberedSeats;

    @OneToMany(mappedBy = "eventSector", cascade = CascadeType.ALL)
    private List<TicketBatch> batches;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "venue_sector_id", nullable = false)
    private VenueSector venueSector;

    public Integer getTotalTickets() {
        return batches.stream()
                .mapToInt(TicketBatch::getTotalTickets)
                .sum();
    }

    public Integer getSoldTickets() {
        return batches.stream()
                .mapToInt(b -> b.getSoldTickets() != null ? b.getSoldTickets() : 0)
                .sum();
    }

    public Integer getAvailableTickets() {
        return getTotalTickets() - getSoldTickets();
    }
}
