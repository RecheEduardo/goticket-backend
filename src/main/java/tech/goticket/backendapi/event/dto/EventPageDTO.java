package tech.goticket.backendapi.event.dto;

import lombok.Getter;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventCategory;
import tech.goticket.backendapi.event.EventImage;
import tech.goticket.backendapi.event.EventSector;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.venue.Venue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        this.statusId = event.getStatus().getStatusID();
        this.eventVisibilityId = event.getEventVisibility().getVisibilityID();

        this.category = new CategoryDTO(event.getCategory());
        this.organizer = new OrganizerDTO(event.getOrganizer());
        this.venue = new VenueDTO(event.getVenue(), venueSectorMapUrl);

        this.sectors = event.getSectors();
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

    private List<EventSector> sectors = new ArrayList<>();;

    private List<EventImage> images = new ArrayList<>();


    public record CategoryDTO(String name, String slug) {
        public CategoryDTO(EventCategory c) {
            this(c.getName(), c.getSlug());
        }
    }

    public record OrganizerDTO(String legalName, String cnpj) {
        public OrganizerDTO(Organizer o) {
            this(o.getLegalName(), o.getCNPJ());
        }
    }

    public record VenueDTO(
            Long venueID, String name, String cnpj, String description,
            String streetAddress, String streetAddressNumber,
            String neighborhood, String city, String state,
            String country, String zipCode, Object status,
            String sectorMapS3Key, String sectorMapUrl) {

        public VenueDTO(Venue v, String sectorMapUrl) {
            this(
                    v.getVenueID(), v.getName(), v.getCNPJ(), v.getDescription(),
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
