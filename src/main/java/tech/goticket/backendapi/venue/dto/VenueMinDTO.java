package tech.goticket.backendapi.venue.dto;

import tech.goticket.backendapi.venue.Venue;

import java.time.Instant;

public record VenueMinDTO(
        Long venueID,
        String name,
        String legalName,
        String CNPJ,
        String city,
        String state,
        String country,
        String statusName,
        String organizerName,
        Instant registerDate
) {
    public VenueMinDTO(Venue venue) {
        this(
                venue.getVenueID(),
                venue.getName(),
                venue.getLegalName(),
                venue.getCNPJ(),
                venue.getCity(),
                venue.getState(),
                venue.getCountry(),
                venue.getStatus() != null ? venue.getStatus().getName() : null,
                venue.getOrganizer() != null ? venue.getOrganizer().getOrganizerName() : null,
                venue.getRegisterDate()
        );
    }
}
