package tech.goticket.backendapi.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.organizer.Organizer;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long eventID;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer ageRestriction;

    @Column(name = "sales_start_date")
    private LocalDateTime salesStartDate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "approval_date", nullable = true)
    private Instant approvalDate;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private EventStatus status;

    @ManyToOne
    @JoinColumn(name = "visibility_id", nullable = false)
    private EventVisibility eventVisibility;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;
}
