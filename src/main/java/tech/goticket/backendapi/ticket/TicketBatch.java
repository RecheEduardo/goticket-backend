package tech.goticket.backendapi.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.event.EventDateSector;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(
        name = "tb_ticket_batches",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ticket_batch_date_sector_number",
                columnNames = {"event_date_sector_id", "batch_number"}
        )
)
@Getter
@Setter
public class TicketBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "batch_number", nullable = false)
    private Integer batchNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "event_date_sector_id", nullable = false)
    private EventDateSector eventDateSector;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 30)
    private List<BatchAllotment> allotments = new ArrayList<>();

    public Integer getSoldTickets() {
        return allotments.stream()
                .mapToInt(a -> a.getSoldTickets() != null ? a.getSoldTickets() : 0)
                .sum();
    }

    public Integer getAvailableTickets() {
        return totalTickets - getSoldTickets();
    }

    public Optional<BatchAllotment> getAllotmentByType(String ticketTypeName) {
        return allotments.stream()
                .filter(a -> Objects.equals(a.getTicketType().getName(), ticketTypeName))
                .findFirst();
    }
}