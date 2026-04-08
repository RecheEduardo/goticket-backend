package tech.goticket.backendapi.event.projection;

import java.time.LocalDateTime;

public interface EventMinProjection {
    Long getEventID();
    String getTitle();
    LocalDateTime getStartDate();
}
