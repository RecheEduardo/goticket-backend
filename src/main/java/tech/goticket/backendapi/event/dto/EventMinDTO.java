package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventImage;
import tech.goticket.backendapi.event.view.EventMinDetailsView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class EventMinDTO {
    private Long eventId;
    private String title;
    private LocalDateTime startDate;
    private Long statusId;
    private Long categoryId;
    private String categoryName;
    private String venueName;
    private String venueCity;
    private String venueState;
    private BigDecimal startingPrice;
    private String[] imageKeys;

    public EventMinDTO(EventMinDetailsView eventMinDetailsView) {
        this.eventId = eventMinDetailsView.getEventId();
        this.title = eventMinDetailsView.getTitle();
        this.startDate = eventMinDetailsView.getStartDate();
        this.categoryId = eventMinDetailsView.getCategoryId();
        this.categoryName = eventMinDetailsView.getCategoryName();
        this.venueName = eventMinDetailsView.getVenueName();
        this.venueCity = eventMinDetailsView.getVenueCity();
        this.venueState = eventMinDetailsView.getVenueState();
        this.startingPrice = eventMinDetailsView.getStartingPrice();
        this.imageKeys = eventMinDetailsView.getImageKeys();
    }

    public EventMinDTO(Event event) {
        this.eventId = event.getEventId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.statusId = event.getStatus().getStatusId();
        this.categoryId = event.getCategory().getCategoryId();
        this.categoryName = event.getCategory().getName();
        this.venueName = event.getVenue().getName();
        this.venueCity = event.getVenue().getCity();
        this.venueState = event.getVenue().getState();

        Optional<EventImage> ei = event.getImages().stream().filter(i -> i.getOrdination().equals(0)).findAny();
        ei.ifPresent(eventImage -> this.imageKeys = new String[]{eventImage.getS3Key()});
    }
}
