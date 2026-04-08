package tech.goticket.backendapi.user.dto;

import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.UserStatus;

import java.util.UUID;

public record UserDTO(UUID userId, String email, Role role, UserStatus status) {
}
