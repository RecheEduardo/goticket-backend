package tech.goticket.backendapi.waitingroom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.shared.exception.QueueAdmissionRequiredException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueGateService {

    private static final String TIER_KEY = "demand:event:%d:tier";

    private final StringRedisTemplate redis;
    private final QueueTokenService tokenService;

    @Value("${goticket.waitingroom.enabled:true}")
    private boolean waitingRoomEnabled;

    public boolean requiresQueue(Long eventId) {
        if (!waitingRoomEnabled) {
            return false;
        }
        String tier = redis.opsForValue().get(String.format(TIER_KEY, eventId));
        return "HIGH".equalsIgnoreCase(tier);
    }

    public void assertAdmitted(String queueToken, UUID userId, Long eventId) {
        if(!requiresQueue(eventId)) {
            return;
        }
        boolean admitted = queueToken != null
                && !queueToken.isBlank()
                && tokenService.isValid(queueToken, userId, eventId);
        if (!admitted) {
            throw new QueueAdmissionRequiredException("Este evento está com alta demanda. Entre na fila para comprar.");
        }
    }
}
