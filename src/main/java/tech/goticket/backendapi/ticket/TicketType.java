package tech.goticket.backendapi.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_ticket_types")
@Getter
@Setter
public class TicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    private String name;

    public boolean isFull()     { return Values.FULL.name().equals(this.name); }
    public boolean isHalf()     { return Values.HALF.name().equals(this.name); }
    public boolean isSolidary() { return Values.SOLIDARY.name().equals(this.name); }

    public enum Values {
        FULL(1L),
        HALF(2L),
        SOLIDARY(3L);

        long ticketTypeId;

        Values(long ticketTypeId){ this.ticketTypeId = ticketTypeId; }

        public long getTicketTypeId() { return ticketTypeId; }
    }
}
