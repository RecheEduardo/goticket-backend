package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.ticket.TicketType;

import java.time.Instant;
import java.util.*;

@Entity
@Table(
        name = "tb_event_date_sectors",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_event_date_sector",
                columnNames = {"event_date_id", "event_sector_id"}
        )
)
@Getter
@Setter
public class EventDateSector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_date_sector_id", nullable = false)
    private Long eventDateSectorID;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_date_id", nullable = false)
    private EventDate eventDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_sector_id", nullable = false)
    private EventSector eventSector;

    @OneToMany(mappedBy = "eventDateSector", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<TicketBatch> batches = new ArrayList<>();

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

    public Optional<BatchAllotment> getCurrentAllotment(TicketType.Values type) {
        return batches.stream()
                .sorted(Comparator.comparing(TicketBatch::getBatchNumber))
                .flatMap(b -> b.getAllotments().stream())
                .filter(a -> type.name().equals(a.getTicketType().getName())
                        && a.getAvailableTickets() > 0)
                .findFirst();
    }

    public Map<TicketType.Values, BatchAllotment> getCurrentAllotmentsByType() {
        Map<TicketType.Values, BatchAllotment> result = new EnumMap<>(TicketType.Values.class);
        for (TicketType.Values type : TicketType.Values.values()) {
            getCurrentAllotment(type).ifPresent(a -> result.put(type, a));
        }
        return result;
    }

}
