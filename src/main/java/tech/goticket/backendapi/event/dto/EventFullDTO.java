package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import tech.goticket.backendapi.event.*;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.ticket.BatchAllotment;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.venue.Venue;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@NoArgsConstructor
public class EventFullDTO {

    public EventFullDTO(Event event, String venueSectorMapUrl) {
        this.eventId = event.getEventId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.ageRestriction = event.getAgeRestriction();
        this.salesStartDate = event.getSalesStartDate();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.approvalDate = event.getApprovalDate();
        this.registerDate = event.getRegisterDate();
        this.lastUpdateDate = event.getLastUpdateDate();

        this.statusId   = event.getStatus().getStatusId();
        this.statusName = event.getStatus().getName();
        this.visibilityId   = event.getEventVisibility().getVisibilityId();
        this.visibilityName = event.getEventVisibility().getName();

        this.category  = new CategoryDTO(event.getCategory());
        this.organizer = new OrganizerDTO(event.getOrganizer());
        this.venue     = new VenueDTO(event.getVenue(), venueSectorMapUrl);

        this.eventDates = event.getEventDates().stream()
                .sorted(Comparator.comparing(EventDate::getStartDate))
                .map(EventDateFullDTO::new)
                .toList();

        this.images = event.getImages().stream()
                .sorted(Comparator.comparing(EventImage::getOrdination))
                .map(ImageDTO::new)
                .toList();
    }

    private Long eventId;
    private String title;
    private String description;
    private Integer ageRestriction;
    private LocalDateTime salesStartDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Instant approvalDate;
    private Instant registerDate;
    private Instant lastUpdateDate;

    private Long statusId;
    private String statusName;
    private Long visibilityId;
    private String visibilityName;

    private CategoryDTO category;
    private OrganizerDTO organizer;
    private VenueDTO venue;
    private List<EventDateFullDTO> eventDates;
    private List<ImageDTO> images;

    public record CategoryDTO(Long id, String name, String slug) {
        public CategoryDTO(EventCategory c) {
            this(c.getCategoryId(), c.getName(), c.getSlug());
        }
    }

    public record OrganizerDTO(String userId, String organizerName, String legalName, String cnpj) {
        public OrganizerDTO(Organizer o) {
            this(
                    o.getUserId() != null ? o.getUserId().toString() : null,
                    o.getOrganizerName(),
                    o.getLegalName(),
                    o.getCNPJ()
            );
        }
    }

    public record VenueDTO(
            Long venueID, String name, String cnpj, String description,
            String streetAddress, String streetAddressNumber,
            String neighborhood, String city, String state,
            String country, String zipCode,
            String sectorMapS3Key, String sectorMapUrl
    ) {
        public VenueDTO(Venue v, String sectorMapUrl) {
            this(
                    v.getVenueId(), v.getName(), v.getCNPJ(), v.getDescription(),
                    v.getStreetAddress(), v.getStreetAddressNumber(),
                    v.getNeighborhood(), v.getCity(), v.getState(),
                    v.getCountry(), v.getZipCode(),
                    v.getSectorMapS3Key(), sectorMapUrl
            );
        }
    }

    public record EventDateFullDTO(
            Long eventDateId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long statusId,
            String statusName,
            Instant registerDate,
            Instant lastUpdateDate,
            List<EventDateSectorFullDTO> dateSectors
    ) {
        public EventDateFullDTO(EventDate ed) {
            this(
                    ed.getEventDateId(),
                    ed.getStartDate(),
                    ed.getEndDate(),
                    ed.getStatus() != null ? ed.getStatus().getStatusId() : null,
                    ed.getStatus() != null ? ed.getStatus().getName() : null,
                    ed.getRegisterDate(),
                    ed.getLastUpdateDate(),
                    ed.getDateSectors().stream()
                            .sorted(Comparator.comparing(eds -> eds.getEventSector().getName()))
                            .map(EventDateSectorFullDTO::new)
                            .toList()
            );
        }
    }

    public record EventDateSectorFullDTO(
            Long eventDateSectorId,
            String name,
            String description,
            boolean hasNumberedSeats,
            Long venueSectorId,
            String mapElementId,
            Integer totalTickets,
            Integer soldTickets,
            Integer availableTickets,
            List<TicketBatchFullDTO> batches
    ) {
        public EventDateSectorFullDTO(EventDateSector eds) {
            this(
                    eds.getEventDateSectorId(),
                    eds.getEventSector().getName(),
                    eds.getEventSector().getDescription(),
                    eds.getEventSector().isHasNumberedSeats(),
                    eds.getEventSector().getVenueSectorId(),
                    eds.getEventSector().getMapElementId(),
                    eds.getTotalTickets(),
                    eds.getSoldTickets(),
                    eds.getAvailableTickets(),
                    eds.getBatches().stream()
                            .sorted(Comparator.comparing(TicketBatch::getBatchNumber))
                            .map(TicketBatchFullDTO::new)
                            .toList()
            );
        }
    }

    public record TicketBatchFullDTO(
            Long batchId,
            Integer batchNumber,
            BigDecimal price,
            LocalDateTime activationDate,
            Integer totalTickets,
            Integer soldTickets,
            Integer availableTickets,
            List<AllotmentFullDTO> allotments  // todos os allotments, não só os correntes
    ) {
        public TicketBatchFullDTO(TicketBatch b) {
            this(
                    b.getBatchId(),
                    b.getBatchNumber(),
                    b.getPrice(),
                    b.getActivationDate(),
                    b.getTotalTickets(),
                    b.getSoldTickets(),
                    b.getAvailableTickets(),
                    b.getAllotments().stream()
                            .sorted(Comparator.comparing(a -> a.getTicketType().getName()))
                            .map(AllotmentFullDTO::new)
                            .toList()
            );
        }
    }

    public record AllotmentFullDTO(
            Long allotmentId,
            Long ticketTypeId,
            String ticketTypeName,
            Integer quota,
            Integer soldTickets,
            Integer available,
            BigDecimal storedPrice,
            BigDecimal effectivePrice
    ) {
        public AllotmentFullDTO(BatchAllotment a) {
            this(
                    a.getAllotmentId(),
                    a.getTicketType().getTicketTypeId(),
                    a.getTicketType().getName(),
                    a.getQuota(),
                    a.getSoldTickets(),
                    a.getAvailableTickets(),
                    a.getPrice(),
                    a.effectivePrice()
            );
        }
    }

    public record ImageDTO(String s3Key, Integer ordination) {
        public ImageDTO(EventImage img) {
            this(img.getS3Key(), img.getOrdination());
        }
    }
}