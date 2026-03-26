package tech.goticket.backendapi.controller.dto;

import tech.goticket.backendapi.entities.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventDTO(String title,
                             String description,
                             Integer ageRestriction,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             UUID organizerID) {
}
