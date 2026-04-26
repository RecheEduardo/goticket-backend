package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.event.projection.EventMinProjection;
import tech.goticket.backendapi.event.view.EventMinDetailsView;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventMinDTO {
    private Long eventID;
    private String title;
    private LocalDateTime startDate;
    private String categoryName;
    private String venueName;
    private String venueCity;
    private String venueState;
    private BigDecimal startingPrice;
    private String[] imageKeys;

    public EventMinDTO() {}

    public EventMinDTO(EventMinProjection eventMinProjection) {
        this.eventID = eventMinProjection.getEventID();
        this.title = eventMinProjection.getTitle();
        this.startDate = eventMinProjection.getStartDate();
        this.venueName = eventMinProjection.getVenue().getName();
        this.venueCity = eventMinProjection.getVenue().getCity();
        this.venueState = eventMinProjection.getVenue().getState();
    }

    public EventMinDTO(EventMinDetailsView eventMinDetailsView) {
        this.eventID = eventMinDetailsView.getEventId();
        this.title = eventMinDetailsView.getTitle();
        this.startDate = eventMinDetailsView.getStartDate();
        this.categoryName = eventMinDetailsView.getCategoryName();
        this.venueName = eventMinDetailsView.getVenueName();
        this.venueCity = eventMinDetailsView.getVenueCity();
        this.venueState = eventMinDetailsView.getVenueState();
        this.startingPrice = eventMinDetailsView.getStartingPrice();
        this.imageKeys = eventMinDetailsView.getImageKeys();
    }
}
