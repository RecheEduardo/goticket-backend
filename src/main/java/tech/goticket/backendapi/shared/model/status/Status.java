package tech.goticket.backendapi.shared.model.status;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_status")
@Getter
@Setter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusID;

    private String name;

    public enum Values {
        ACTIVE(1L),
        INACTIVE(2L);

        long statusID;

        Values(long statusID){ this.statusID = statusID; }

        public long getStatusID() { return statusID; }
    }
}
