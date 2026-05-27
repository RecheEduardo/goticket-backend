package tech.goticket.backendapi.client.dto;

import tech.goticket.backendapi.client.Client;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClientProfileDTO(
        UUID userId,
        String email,
        String fullName,
        Integer sex,
        String identityDocument,
        LocalDate birthDate,
        Instant registerDate,
        Instant lastUpdateDate,
        String streetAddress,
        String streetAddressNumber,
        String neighborhood,
        String city,
        String state,
        String country,
        String zipCode,
        String roleName,
        String statusName
) {
    public ClientProfileDTO(Client client) {
        this(
                client.getUserId(),
                client.getEmail(),
                client.getFullName(),
                client.getSex(),
                client.getIdentityDocument(),
                client.getBirthDate(),
                client.getRegisterDate(),
                client.getLastUpdateDate(),
                client.getStreetAddress(),
                client.getStreetAddressNumber(),
                client.getNeighborhood(),
                client.getCity(),
                client.getState(),
                client.getCountry(),
                client.getZipCode(),
                client.getRole() != null ? client.getRole().getName() : null,
                client.getStatus() != null ? client.getStatus().getName() : null
        );
    }
}
