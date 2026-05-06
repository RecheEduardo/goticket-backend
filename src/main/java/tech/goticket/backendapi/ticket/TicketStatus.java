package tech.goticket.backendapi.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_ticket_status")
@Getter
@Setter
public class TicketStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_status_id")
    private Long ticketStatusId;

    private String name;

    public boolean isActive()   { return Values.ACTIVE.name().equals(this.name); }
    public boolean isUsed()     { return Values.USED.name().equals(this.name); }
    public boolean isCanceled() { return Values.CANCELED.name().equals(this.name); }
    public boolean isRefunded() { return Values.REFUNDED.name().equals(this.name); }
    public boolean isTransferred() { return Values.TRANSFERRED.name().equals(this.name); }

    public enum Values {
        ACTIVE(1L),
        USED(2L),
        CANCELED(3L),
        REFUNDED(4L),
        TRANSFERRED(5L);

        long ticketStatusId;

        Values(long ticketStatusId){ this.ticketStatusId = ticketStatusId; }

        public long getTicketStatusId() { return ticketStatusId; }
    }
}
