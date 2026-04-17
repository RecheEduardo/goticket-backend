package tech.goticket.backendapi.user.dto;

import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.shared.model.status.Status;

import java.util.UUID;

public record UserDTO(UUID userId, String email, Role role, Status status) {
}
