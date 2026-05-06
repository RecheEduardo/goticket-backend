package tech.goticket.backendapi.event.enums;

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
    private Long statusId;

    private String name;

    public enum Values {
        PENDING_APPROVAL(1L),
        APPROVED(2L),
        COMPLETED(3L),
        DECLINED(4L),
        CANCELED(5L),
        POSTPONED(6L);

        long statusId;

        Values(long statusID){ this.statusId = statusID; }

        public long getStatusId() { return statusId; }
    }

    public boolean isApproved() {
        if (this.statusId != null) {
            return this.statusId.equals(Values.APPROVED.getStatusId());
        }
        if (this.name != null) {
            return this.name.equals(Values.APPROVED.name());
        }
        return false;
    }

    public boolean isPostponed() {
        if (this.statusId != null) {
            return this.statusId.equals(Values.POSTPONED.getStatusId());
        }
        if (this.name != null) {
            return this.name.equals(Values.POSTPONED.name());
        }
        return false;
    }
}
