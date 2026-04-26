package tech.goticket.backendapi.event.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Subselect("SELECT * FROM vw_event_min_details")
@Synchronize("vw_event_min_details")
@Immutable
@Getter
public class EventMinDetailsView {
    @Id
    @Column(name = "event_id")
    private Long eventId;

    private String title;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "venue_city")
    private String venueCity;

    @Column(name = "venue_state")
    private String venueState;

    @Column(name = "starting_price")
    private BigDecimal startingPrice;

    @Column(name = "image_keys", columnDefinition = "text[]")
    private String[] imageKeys;
}
