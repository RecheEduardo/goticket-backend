package tech.goticket.backendapi.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.event.EventSector;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_ticket_batches",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ticket_batch_sector_number",
                columnNames = {"event_sector_id", "batch_number"}
        )
)
@Getter
@Setter
public class TicketBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id", nullable = false)
    private Long batchID;

    @Column(name = "batch_number", nullable = false)
    private Integer batchNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "sold_tickets")
    private Integer soldTickets;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "event_sector_id", nullable = false)
    private EventSector eventSector;

    public Integer getAvailableTickets() {
        return totalTickets - (soldTickets != null ? soldTickets : 0);
    }
}