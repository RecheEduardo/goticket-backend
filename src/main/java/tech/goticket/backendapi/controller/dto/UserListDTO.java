package tech.goticket.backendapi.controller.dto;

import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.UserStatus;

import java.util.UUID;

public record UserListDTO(UUID userId, String email, Role role, UserStatus status) {
}
