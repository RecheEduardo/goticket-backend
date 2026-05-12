package tech.goticket.backendapi.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.ticket.enums.TicketType;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "tb_batch_allotments",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_batch_allotment_type",
                columnNames = {"batch_id", "ticket_type_id"}
        )
)
@BatchSize(size = 50)
@Getter
@Setter
public class BatchAllotment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allotment_id", nullable = false)
    private Long allotmentId;

    @Column(nullable = false)
    private Integer quota;

    @Column(name = "sold_tickets", nullable = false)
    private Integer soldTickets = 0;

    @Column(name = "reserved_tickets", nullable = false)
    private Integer reservedTickets = 0;

    // null para FULL/HALF (derivam do batch.price); obrigatório para SOLIDARY
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    // Lock otimista — protege contra overbooking sob concorrência
    @Version
    @JsonIgnore
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "batch_id", nullable = false)
    private TicketBatch batch;

    public BigDecimal effectivePrice() {
        if (ticketType == null) {
            throw new IllegalStateException("BatchAllotment sem tipo definido.");
        }
        if (ticketType.isFull())     return batch.getPrice();
        if (ticketType.isHalf())     return batch.getPrice().multiply(new BigDecimal("0.5"));
        if (ticketType.isSolidary()) return price;
        throw new IllegalStateException("Tipo de ingresso desconhecido: " + ticketType.getName());
    }

    public Integer getAvailableTickets() {
        int sold = soldTickets != null ? soldTickets : 0;
        int reserved = reservedTickets != null ? reservedTickets : 0;
        return quota - sold - reserved;
    }
}