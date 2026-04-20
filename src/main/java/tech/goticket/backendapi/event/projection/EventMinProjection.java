package tech.goticket.backendapi.event.projection;

import java.time.LocalDateTime;

public interface EventMinProjection {
    Long getEventID();
    String getTitle();
    LocalDateTime getStartDate();
    VenueInfo getVenue();

    interface VenueInfo {
        String getName();
        String getCity();
        String getState();
    }
}
