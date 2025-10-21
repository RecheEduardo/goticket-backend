package tech.goticket.backendapi.controller.dto;

import tech.goticket.backendapi.entities.User;

import java.time.Instant;
import java.util.UUID;

public record CreateEventDTO(Long eventID,
                             UUID organizerID,
                             String eventTitle,
                             String eventDescription    ) {
}
