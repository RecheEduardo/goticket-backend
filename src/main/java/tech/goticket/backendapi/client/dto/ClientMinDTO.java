package tech.goticket.backendapi.client.dto;

import tech.goticket.backendapi.client.Client;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClientMinDTO(
        UUID userID,
        String email,
        String fullName,
        String identityDocument,
        Integer sex,
        LocalDate birthDate,
        String city,
        String state,
        String statusName,
        Instant registerDate
) {
    public ClientMinDTO(Client client) {
        this(
                client.getUserID(),
                client.getEmail(),
                client.getFullName(),
                client.getIdentityDocument(),
                client.getSex(),
                client.getBirthDate(),
                client.getCity(),
                client.getState(),
                client.getStatus() != null ? client.getStatus().getName() : null,
                client.getRegisterDate()
        );
    }
}
