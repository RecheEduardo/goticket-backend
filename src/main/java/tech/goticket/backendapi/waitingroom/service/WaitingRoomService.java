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
    private static final String ADMIT_KEY   = "admit:event:%d:user:%s";
    private static final String ACTIVE_SET  = "waitingroom:active-events";

    private final StringRedisTemplate redis;
    private final QueueTokenService tokenService;

    @Value("${goticket.waitingroom.admission-ttl-seconds:600}")
    private long admissionTtlSeconds;

    @Value("${goticket.waitingroom.admit-batch-size:100}")
    private int admitBatchSize;

    private String queueKey(Long eventId)  { return String.format(QUEUE_KEY, eventId); }
    private String admitKey(Long e, UUID u) { return String.format(ADMIT_KEY, e, u); }

    public QueueStatusResponse enqueue(Long eventId, UUID userId) {
        if (redis.hasKey(admitKey(eventId, userId))) {
            return QueueStatusResponse.admitted(eventId, tokenService.issue(userId, eventId));
        }

        String key = queueKey(eventId);
        double score = Instant.now().toEpochMilli();

        redis.opsForZSet().addIfAbsent(key, userId.toString(), score);
        redis.opsForSet().add(ACTIVE_SET, eventId.toString());

        return buildWaitingResponse(eventId, key, userId);
    }

    public QueueStatusResponse getStatus(Long eventId, UUID userId) {
        if (redis.hasKey(admitKey(eventId, userId))) {
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

    public Integer admitBatch(Long eventId, int batchSize) {
        String key = queueKey(eventId);

        Set<ZSetOperations.TypedTuple<String>> popped = redis.opsForZSet().popMin(key, batchSize);

        if (popped == null || popped.isEmpty()) {
            redis.opsForSet().remove(ACTIVE_SET, eventId.toString());
            return 0;
        }

        for (ZSetOperations.TypedTuple<String> tuple : popped) {
            String userId = tuple.getValue();
            redis.opsForValue().set(
                    String.format(ADMIT_KEY, eventId, userId),
                    "1",
                    Duration.ofSeconds(admissionTtlSeconds)
            );
        }

        log.info("WaitingRoom: admitidos {} usuário(s) do evento {}", popped.size(), eventId);
        return popped.size();
    }
}
