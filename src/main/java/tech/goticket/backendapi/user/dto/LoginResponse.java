package tech.goticket.backendapi.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn) {
}
