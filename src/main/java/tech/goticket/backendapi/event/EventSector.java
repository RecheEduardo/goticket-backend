package tech.goticket.backendapi.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.sector.Sector;
import tech.goticket.backendapi.ticket.TicketBatch;

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
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

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
