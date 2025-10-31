package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_event_status")
@Getter
@Setter
public class EventStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusID;

    private String name;

    public enum Values {
        PENDING_APPROVAL(1L),
        APPROVED(2L),
        COMPLETED(3L),
        DECLINED(4L),
        CANCELED(5L),
        POSTPONED(6L);

        long statusID;

        Values(long statusID){ this.statusID = statusID; }

        public long getStatusID() { return statusID; }
    }
}
