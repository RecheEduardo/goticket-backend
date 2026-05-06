package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import tech.goticket.backendapi.event.*;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.ticket.enums.TicketType;
import tech.goticket.backendapi.venue.Venue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EventPageDTO {

    public EventPageDTO() {}

    public EventPageDTO(Event event, String venueSectorMapUrl) {
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.ageRestriction = event.getAgeRestriction();
        this.salesStartDate = event.getSalesStartDate();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.approvalDate = event.getApprovalDate();
        this.statusId = event.getStatus().getStatusId();
        this.eventVisibilityId = event.getEventVisibility().getVisibilityId();

        this.category = new CategoryDTO(event.getCategory());
        this.organizer = new OrganizerDTO(event.getOrganizer());
        this.venue = new VenueDTO(event.getVenue(), venueSectorMapUrl);

        this.dates = event.getEventDates()
                .stream()
                .map(EventDateDTO::new)
                .toList();
        this.images = event.getImages();
    }

    private String title;

    private String description;

    private Integer ageRestriction;

    private LocalDateTime salesStartDate;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Instant approvalDate;

    private Long statusId;

    private Long eventVisibilityId;

    private CategoryDTO category;

    private OrganizerDTO organizer;

    private VenueDTO venue;

    private List<EventDateDTO> dates = new ArrayList<>();

    private List<EventImage> images = new ArrayList<>();


    public record CategoryDTO(String name, String slug) {
        public CategoryDTO(EventCategory c) {
            this(c.getName(), c.getSlug());
        }
    }

    public record EventDateSectorDTO(
            Long eventDateSectorID,
            String name,
            String description,
            boolean hasNumberedSeats,
            Long venueSectorId,
            String mapElementId,
            Integer totalTickets,
            Integer availableTickets,
            Map<TicketType.Values, AllotmentDTO> currentAllotments) {

        public EventDateSectorDTO(EventDateSector eds) {
            this(
                    eds.getEventDateSectorId(),
                    eds.getEventSector().getName(),
                    eds.getEventSector().getDescription(),
                    eds.getEventSector().isHasNumberedSeats(),
                    eds.getEventSector().getVenueSectorId(),
                    eds.getEventSector().getMapElementId(),
                    eds.getTotalTickets(),
                    eds.getAvailableTickets(),
                    eds.getCurrentAllotmentsByType().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> new AllotmentDTO(e.getValue()),
                                    (a, b) -> a,
                                    () -> new EnumMap<>(TicketType.Values.class)
                            ))
            );
        }
    }

    public record AllotmentDTO(
            Long allotmentID,
            Integer batchNumber,
            BigDecimal price,
            Integer availableTickets) {

        public AllotmentDTO(BatchAllotment a) {
            this(
                    a.getAllotmentId(),
                    a.getBatch().getBatchNumber(),
                    a.effectivePrice(),
                    a.getAvailableTickets()
            );
        }
    }

    public record OrganizerDTO(String legalName, String cnpj) {
        public OrganizerDTO(Organizer o) {
            this(o.getLegalName(), o.getCNPJ());
        }
    }

    public record VenueDTO(
            Long venueId, String name, String cnpj, String description,
            String streetAddress, String streetAddressNumber,
            String neighborhood, String city, String state,
            String country, String zipCode, Object status,
            String sectorMapS3Key, String sectorMapUrl) {

        public VenueDTO(Venue v, String sectorMapUrl) {
            this(
                    v.getVenueId(), v.getName(), v.getCNPJ(), v.getDescription(),
                    v.getStreetAddress(), v.getStreetAddressNumber(),
                    v.getNeighborhood(), v.getCity(), v.getState(),
                    v.getCountry(), v.getZipCode(),
                    v.getStatus(),
                    v.getSectorMapS3Key(),
                    sectorMapUrl
            );
        }
    }
}
