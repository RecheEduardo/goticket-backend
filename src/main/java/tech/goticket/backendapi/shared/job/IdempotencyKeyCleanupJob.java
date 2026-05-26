package tech.goticket.backendapi.shared.job;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.goticket.backendapi.idempotency.IdempotencyKeyRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class IdempotencyKeyCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(IdempotencyKeyCleanupJob.class);

    private final IdempotencyKeyRepository repository;

    @Scheduled(fixedDelay = 3_600_000, initialDelay = 60_000)
    @Transactional
    public void cleanup() {
        Long deleted = repository.deleteByExpiresAtBefore(Instant.now());
        if (deleted > 0) {
            log.info("IdempotencyKeyCleanupJob: {} chave(s) expirada(s) removida(s).", deleted);
        }
    }
}