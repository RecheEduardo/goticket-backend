package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "event_id", nullable = false)
    private Long eventID;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizerID;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;

    @Column(name = "event_description", nullable = false)
    private String eventDescription;

    @CreationTimestamp
    @Column(name = "creation_timestamp", nullable = false)
    private Instant creationTimeStamp;

}
