package tech.goticket.backendapi.venue.dto;

import tech.goticket.backendapi.venue.Venue;

import java.time.Instant;
import java.util.UUID;

public record VenueDetailDTO(
        Long venueID,
        String name,
        String legalName,
        String CNPJ,
        String description,
        String streetAddress,
        String streetAddressNumber,
        String neighborhood,
        String city,
        String state,
        String country,
        String zipCode,
        String sectorMapS3Key,
        Instant approvalDate,
        Instant registerDate,
        Instant lastUpdateDate,
        StatusRef status,
        OrganizerRef organizer
) {
    public record StatusRef(Long statusID, String name) {}

    public record OrganizerRef(UUID userID, String organizerName, String legalName, String CNPJ) {}

    public static VenueDetailDTO fromEntity(Venue venue) {
        StatusRef statusRef = venue.getStatus() == null ? null
                : new StatusRef(venue.getStatus().getStatusID(), venue.getStatus().getName());

        OrganizerRef organizerRef = venue.getOrganizer() == null ? null
                : new OrganizerRef(
                        venue.getOrganizer().getUserID(),
                        venue.getOrganizer().getOrganizerName(),
                        venue.getOrganizer().getLegalName(),
                        venue.getOrganizer().getCNPJ()
                );

        return new VenueDetailDTO(
                venue.getVenueID(),
                venue.getName(),
                venue.getLegalName(),
                venue.getCNPJ(),
                venue.getDescription(),
                venue.getStreetAddress(),
                venue.getStreetAddressNumber(),
                venue.getNeighborhood(),
                venue.getCity(),
                venue.getState(),
                venue.getCountry(),
                venue.getZipCode(),
                venue.getSectorMapS3Key(),
                venue.getApprovalDate(),
                venue.getRegisterDate(),
                venue.getLastUpdateDate(),
                statusRef,
                organizerRef
        );
    }
}
