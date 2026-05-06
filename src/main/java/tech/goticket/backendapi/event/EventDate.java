package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.event.enums.EventStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "tb_event_dates")
@Entity
@Getter
@Setter
public class EventDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_date_id", nullable = false)
    private Long eventDateId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private EventStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "eventDate", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 30)
    private List<EventDateSector> dateSectors = new ArrayList<>();
}
