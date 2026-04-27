package tech.goticket.backendapi.shared.job;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.goticket.backendapi.user.token.RefreshTokenRepository;

import java.time.Instant;

@Component
public class RefreshTokenCleanupJob {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpired(Instant.now());
        if (deleted > 0) {
            System.out.println("Refresh tokens expirados removidos: " + deleted);
        }
    }
}
