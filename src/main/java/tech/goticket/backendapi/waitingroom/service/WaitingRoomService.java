package tech.goticket.backendapi.waitingroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.waitingroom.dto.QueueStatusResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingRoomService {

    private static final String QUEUE_KEY   = "queue:event:%d";
    private static final String ADMITTED_KEY   = "admitted:event:%d";
    private static final String ACTIVE_SET  = "waitingroom:active-events";

    private final StringRedisTemplate redis;
    private final QueueTokenService tokenService;
    private final QueueGateService queueGateService;

    @Value("${goticket.waitingroom.admission-ttl-seconds:600}")
    private long admissionTtlSeconds;

    @Value("${goticket.waitingroom.admit-batch-size:100}")
    private int admitBatchSize;

    @Value("${goticket.waitingroom.max-active:500}")
    private int maxActive;

    private String queueKey(Long eventId)  { return String.format(QUEUE_KEY, eventId); }
    private String admittedKey(Long e) { return String.format(ADMITTED_KEY, e); }

    public QueueStatusResponse enqueue(Long eventId, UUID userId) {
        if (!queueGateService.requiresQueue(eventId)) {
            return QueueStatusResponse.admitted(eventId, tokenService.issue(userId, eventId));
        }

        if (isAdmitted(eventId, userId)) {
            return QueueStatusResponse.admitted(eventId, tokenService.issue(userId, eventId));
        }

        String key = queueKey(eventId);
        double score = Instant.now().toEpochMilli();

        redis.opsForZSet().addIfAbsent(key, userId.toString(), score);
        redis.opsForSet().add(ACTIVE_SET, eventId.toString());

        return buildWaitingResponse(eventId, key, userId);
    }

    public QueueStatusResponse getStatus(Long eventId, UUID userId) {
        if (isAdmitted(eventId, userId)) {
            return QueueStatusResponse.admitted(eventId, tokenService.issue(userId, eventId));
        }

        String key = queueKey(eventId);
        Long rank = redis.opsForZSet().rank(key, userId.toString());
        if (rank == null) {
            return null;
        }
        return buildWaitingResponse(eventId, key, userId);
    }

    private QueueStatusResponse buildWaitingResponse(Long eventId, String key, UUID userId) {
        Long rank = redis.opsForZSet().rank(key, userId.toString());
        long position = (rank == null) ? 1 : rank + 1;
        Long size = redis.opsForZSet().size(key);
        long total = (size == null) ? position : size;
        long waitSeconds = estimateWait(position);
        return QueueStatusResponse.waiting(eventId, position, total, waitSeconds);
    }

    private long estimateWait(long position) {
        long batchesAhead = (position + admitBatchSize - 1) / admitBatchSize;
        return batchesAhead * 5L;  // 5s = intervalo do job, ajustar no caso de mudança
    }

    private boolean isAdmitted(Long eventId, UUID userId) {
        Double score = redis.opsForZSet().score(admittedKey(eventId), userId.toString());
        return score != null && score >= Instant.now().toEpochMilli();
    }

    public Integer admitBatch(Long eventId, int batchSize) {
        String admitted = admittedKey(eventId);
        long now = Instant.now().toEpochMilli();

        redis.opsForZSet().removeRangeByScore(admitted, Double.NEGATIVE_INFINITY, now);
        Long active = redis.opsForZSet().zCard(admitted);
        long vagas = maxActive - (active == null ? 0 : active);
        if (vagas <= 0) return 0;

        long toAdmit = Math.min(vagas, batchSize);
        Set<ZSetOperations.TypedTuple<String>> popped = redis.opsForZSet().popMin(queueKey(eventId), toAdmit);
        if (popped == null || popped.isEmpty()) {
            if (active == null || active == 0) redis.opsForSet().remove(ACTIVE_SET, eventId.toString());
            return 0;
        }

        double expiry = now + Duration.ofSeconds(admissionTtlSeconds).toMillis();
        for (var t : popped) {
            if (t.getValue() != null) redis.opsForZSet().add(admitted, t.getValue(), expiry);
        }
        log.info("WaitingRoom: admitidos {} (pool {}/{}) do evento {}", popped.size(), active, maxActive, eventId);
        return popped.size();
    }

    public void releaseSlot(Long eventId, UUID userId) {
        redis.opsForZSet().remove(admittedKey(eventId), userId.toString());
    }
}
