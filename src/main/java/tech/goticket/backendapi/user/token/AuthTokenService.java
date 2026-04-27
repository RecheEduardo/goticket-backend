package tech.goticket.backendapi.user.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import tech.goticket.backendapi.shared.exception.user.RefreshTokenReuseException;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.dto.LoginResponse;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthTokenService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Value("${jwt.access.ttl:900}")
    private long accessTtl;

    /**
     * Gera par access + refresh para login ou cadastro (nova família).
     */
    @Transactional
    public LoginResponse issueTokens(User user) {
        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createForLogin(user);
        return new LoginResponse(accessToken, refreshToken.getToken(), accessTtl);
    }

    /**
     * Rotaciona refresh token e gera novo access token.
     */
    @Transactional
    public LoginResponse refreshTokens(String refreshTokenValue) {
        RefreshToken newRefresh = refreshTokenService.rotate(refreshTokenValue);

        User user = newRefresh.getUser();
        user.validateUserStatus();

        String accessToken = generateAccessToken(newRefresh.getUser());
        return new LoginResponse(accessToken, newRefresh.getToken(), accessTtl);
    }

    /**
     * Revoga todos os refresh tokens de um usuário.
     */
    @Transactional
    public void revokeAll(UUID userId) {
        refreshTokenService.revokeAllUserTokens(userId);
    }

    private String generateAccessToken(User user) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("goticketbackend")
                .subject(user.getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTtl))
                .claim("scope", user.getRole().getName())
                .claim("name", user.displayName())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
