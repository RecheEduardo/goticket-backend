package tech.goticket.backendapi.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "event_id")
    private Long eventID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User organizer;

    @Column(name = "event_description")
    private String eventDescription;

    @CreationTimestamp
    private Instant creationTimeStamp;

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Instant getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Instant creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }
}
