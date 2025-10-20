package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_user_status")
@Getter
@Setter
public class UserStatus {
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
