package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_event_visibilities")
@Getter
@Setter
public class EventVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visibility_id")
    private Long visibilityID;

    private String name;

    public enum Values {
        PUBLIC(1L),
        PRIVATE(2L);

        long visibilityId;

        Values(long visibilityId){ this.visibilityId = visibilityId; }

        public long getVisibilityId() { return visibilityId; }
    }
}
