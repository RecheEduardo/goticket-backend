package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.event.projection.EventMinProjection;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventMinDTO {
    private Long eventID;
    private String title;
    private LocalDateTime startDate;

    public EventMinDTO() {}

    public EventMinDTO(EventMinProjection eventMinProjection) {
        this.eventID = eventMinProjection.getEventID();
        this.title = eventMinProjection.getTitle();
        this.startDate = eventMinProjection.getStartDate();
    }
}
