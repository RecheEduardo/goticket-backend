package tech.goticket.backendapi.shared.job;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.order.service.OrderExpirationService;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpireOrdersJob {
    private static final Logger log = LoggerFactory.getLogger(ExpireOrdersJob.class);
    private static final int BATCH_SIZE = 50;

    private final OrderRepository orderRepository;
    private final OrderExpirationService orderExpirationService;

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void expireOrders() {
        Instant now = Instant.now();
        List<Long> orders = orderRepository.findOrderIdsToExpire("PENDING_PAYMENT", now, BATCH_SIZE);

        if (orders.isEmpty()) return;

        log.info("ExpireOrdersJob: {} order(s) candidata(s) a expirar", orders.size());

        int expired = 0, raced = 0, failed = 0;
        for (Long orderId : orders) {
            try {
                boolean wasExpired = orderExpirationService.expireIfStillEligible(orderId);
                if (wasExpired) expired++;
                else raced++;
            } catch (OptimisticLockingFailureException e) {
                raced++;
            } catch (Exception e) {
                failed++;
                log.error("ExpireOrdersJob: falha ao expirar order {}: {}", orderId, e.getMessage(), e);
            }
        }

        log.info("ExpireOrdersJob: expired={}, raced={}, failed={}", expired, raced, failed);
    }
}
