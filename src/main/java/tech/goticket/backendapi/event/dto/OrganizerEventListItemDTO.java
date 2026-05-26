package tech.goticket.backendapi.event.dto;

import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventImage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record OrganizerEventListItemDTO(
        Long eventId,
        String title,
        String statusName,
        String visibilityName,
        String categoryName,
        String venueName,
        String venueCity,
        String venueState,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Instant registerDate,
        Instant lastUpdateDate,
        Instant approvalDate,
        String mainImageKey
) {
    public OrganizerEventListItemDTO(Event event) {
        this(
                event.getEventId(),
                event.getTitle(),
                event.getStatus() != null ? event.getStatus().getName() : null,
                event.getEventVisibility() != null ? event.getEventVisibility().getName() : null,
                event.getCategory() != null ? event.getCategory().getName() : null,
                event.getVenue() != null ? event.getVenue().getName() : null,
                event.getVenue() != null ? event.getVenue().getCity() : null,
                event.getVenue() != null ? event.getVenue().getState() : null,
                event.getStartDate(),
                event.getEndDate(),
                event.getRegisterDate(),
                event.getLastUpdateDate(),
                event.getApprovalDate(),
                extractMainImageKey(event.getImages())
        );
    }

    private static String extractMainImageKey(List<EventImage> images) {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .sorted(Comparator.comparing(
                        EventImage::getOrdination,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EventImage::getS3Key)
                .findFirst()
                .orElse(null);
    }
}
