package tech.goticket.backendapi.controller.projections;

import java.time.LocalDateTime;

public interface EventMinProjection {
    Long getEventID();
    String getTitle();
    LocalDateTime getStartDate();
}
