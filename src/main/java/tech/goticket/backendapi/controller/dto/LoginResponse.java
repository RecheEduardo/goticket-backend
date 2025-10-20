package tech.goticket.backendapi.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
