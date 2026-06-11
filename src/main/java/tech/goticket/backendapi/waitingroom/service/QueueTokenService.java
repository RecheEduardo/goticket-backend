package tech.goticket.backendapi.waitingroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueTokenService {

    private static final String PURPOSE = "queue-admission";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${goticket.waitingroom.token-ttl-seconds:600}")
    private long tokenTtlSeconds;

    public String issue(UUID userId, Long eventId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("goticketbackend")
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenTtlSeconds))
                .claim("purpose", PURPOSE)
                .claim("eventId", eventId)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isValid(String token, UUID userId, Long eventId) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return PURPOSE.equals(jwt.getClaimAsString("purpose"))
                    && userId.toString().equals(jwt.getSubject())
                    && eventId.equals(((Number) jwt.getClaim("eventId")).longValue());
        }
        catch (JwtException e) {
            return false;
        }
    }
}
