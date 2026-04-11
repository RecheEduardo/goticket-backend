package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_event_images")
@Getter
@Setter
public class EventImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventImageID;

    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @Column(name = "is_main_image")
    private boolean isMainImage;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    private Event event;
}
