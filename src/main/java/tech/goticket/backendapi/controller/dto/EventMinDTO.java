package tech.goticket.backendapi.controller.dto;

import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.controller.projections.EventMinProjection;

import java.time.Instant;
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
