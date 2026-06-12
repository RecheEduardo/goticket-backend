package tech.goticket.backendapi.demand.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.demand.EventDemandProfile;
import tech.goticket.backendapi.demand.repository.EventDemandProfileRepository;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.ticket.repository.BatchAllotmentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandDetectionService {

    private static final String TIER_KEY = "demand:event:%d:tier";

    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final BatchAllotmentRepository batchAllotmentRepository;
    private final EventDemandProfileRepository profileRepository;
    private final StringRedisTemplate redis;

    @Value("${goticket.demand.velocity-threshold:30}")        private double velocityThreshold;
    @Value("${goticket.demand.occupancy-high:0.90}")          private double occupancyHigh;
    @Value("${goticket.demand.occupancy-warn:0.70}")          private double occupancyWarn;
    @Value("${goticket.demand.time-window-days:7}")           private long   timeWindowDays;
    @Value("${goticket.demand.tier-mirror-ttl-seconds:180}")  private long   tierMirrorTtl;
    @Value("${goticket.demand.pre-arm-lead-minutes:10}")      private long   preArmLeadMinutes;
    @Value("${goticket.demand.pre-arm-initial-minutes:30}")   private long   preArmInitialMinutes;

    @Transactional
    public void recompute(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) return;

        EventDemandProfile profile = profileRepository.findByEventId(eventId)
                .orElseGet(() -> { var p = new EventDemandProfile(); p.setEventId(eventId); return p; });

        if (profile.isManual()
                && profile.getManualOverrideUntil() != null
                && profile.getManualOverrideUntil().isAfter(Instant.now())) {
            mirrorToRedis(eventId, profile.isHigh());
            return;
        }

        if (event.isExpectedHighDemand() && inPreArmWindow(event.getSalesStartDate())) {
            profile.setTier("HIGH");
            profile.setSource("SCHEDULED");
            profile.setLastEvaluatedAt(Instant.now());
            profileRepository.save(profile);
            mirrorToRedis(eventId, true);
            log.info("Evento {} pré-armado (SCHEDULED) na janela de abertura de vendas", eventId);
            return;
        }

        long ordersLast10 = orderRepository.countOrdersSince(eventId,
                Instant.now().minus(Duration.ofMinutes(10)));
        double velocity = ordersLast10 / 10.0;

        var occ = batchAllotmentRepository.occupancy(eventId);
        long occupied = occ.getOccupied();
        long total    = occ.getTotal();
        double occupancy = (total == 0) ? 0.0 : (double) occupied / total;

        boolean closeToEvent = event.getStartDate() != null
                && event.getStartDate().isBefore(LocalDateTime.now().plusDays(timeWindowDays));

        boolean shouldBeHigh =
                velocity >= velocityThreshold
                || (occupancy >= occupancyWarn && closeToEvent)
                || occupancy >= occupancyHigh;

        String newTier;
        if (profile.isHigh() && !shouldBeHigh) {
            newTier = (ordersLast10 == 0) ? "NORMAL" : "HIGH";
        }
        else {
            newTier = shouldBeHigh ? "HIGH" : "NORMAL";
        }

        profile.setTier(newTier);
        profile.setSource("AUTO");
        profile.setSalesVelocityPerMin(BigDecimal.valueOf(velocity).setScale(2, RoundingMode.HALF_UP));
        profile.setOccupancyRate(BigDecimal.valueOf(occupancy).setScale(4, RoundingMode.HALF_UP));
        profile.setLastEvaluatedAt(Instant.now());
        profileRepository.save(profile);

        mirrorToRedis(eventId, "HIGH".equals(newTier));
    }

    private boolean inPreArmWindow(LocalDateTime salesStart) {
        if (salesStart == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(salesStart.minusMinutes(preArmLeadMinutes))
                && now.isBefore(salesStart.plusMinutes(preArmInitialMinutes));
    }

    private void mirrorToRedis(Long eventId, boolean high) {
        String key = String.format(TIER_KEY, eventId);
        if (high) {
            redis.opsForValue().set(key, "HIGH", Duration.ofSeconds(tierMirrorTtl));
        }
        else {
            redis.delete(key);
        }
    }
}
