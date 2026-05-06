package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.event.enums.EventStatus;
import tech.goticket.backendapi.event.enums.EventVisibility;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.venue.Venue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer ageRestriction;

    @Column(name = "sales_start_date")
    private LocalDateTime salesStartDate;

    @Column(name = "start_date", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime endDate;

    @Column(name = "approval_date")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant approvalDate;

    @Column(name = "register_date", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant lastUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private EventCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visibility_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EventVisibility eventVisibility;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @BatchSize(size = 20)
    private List<EventDate> eventDates = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventSector> sectors = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @BatchSize(size = 20)
    private List<EventImage> images = new ArrayList<>();

    public void recalculateDateRange() {
        if (eventDates == null || eventDates.isEmpty()) {
            return;
        }

        this.startDate = eventDates.stream()
                .map(EventDate::getStartDate)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        this.endDate = eventDates.stream()
                .map(EventDate::getEndDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        this.lastUpdateDate = Instant.now();
    }
}
