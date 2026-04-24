package tech.goticket.backendapi.organizer.dto;

import tech.goticket.backendapi.organizer.Organizer;

import java.time.Instant;
import java.util.UUID;

public record OrganizerMinDTO(
        UUID userID,
        String email,
        String organizerName,
        String legalName,
        String CNPJ,
        String city,
        String state,
        String statusName,
        Instant registerDate
) {
    public OrganizerMinDTO(Organizer organizer) {
        this(
                organizer.getUserID(),
                organizer.getEmail(),
                organizer.getOrganizerName(),
                organizer.getLegalName(),
                organizer.getCNPJ(),
                organizer.getCity(),
                organizer.getState(),
                organizer.getStatus() != null ? organizer.getStatus().getName() : null,
                organizer.getRegisterDate()
        );
    }
}
