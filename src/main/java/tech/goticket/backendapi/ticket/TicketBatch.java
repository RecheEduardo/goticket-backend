package tech.goticket.backendapi.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.event.EventSector;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_ticket_batches")
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

    @Column(name = "total_tickets", nullable = false)
    private Integer totalTickets;

    @Column(name = "sold_tickets")
    private Integer soldTickets;

    @ManyToOne
    @JoinColumn(name = "event_sector_id", nullable = false)
    private EventSector eventSector;

    public Integer getAvailableTickets() {
        return totalTickets - (soldTickets != null ? soldTickets : 0);
    }
}