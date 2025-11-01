package tech.goticket.backendapi.controller.projections;

import java.time.Instant;

public interface EventMinProjection {
    Long getEventID();
    String getTitle();
    Instant getStartDate();
}
