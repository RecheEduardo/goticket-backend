package tech.goticket.backendapi.shared.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.goticket.backendapi.demand.service.DemandDetectionService;
import tech.goticket.backendapi.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecomputeEventDemandJob {
    private final EventRepository eventRepository;
    private final DemandDetectionService demandDetectionService;

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    public void recompute() {
        List<Long> ids = eventRepository.findActiveEventIdsForDemand(LocalDateTime.now());
        for (Long id : ids) {
            try {
                demandDetectionService.recompute(id);
            } catch (Exception e) {
                log.error("Demand recompute falhou p/ evento {}: {}", id, e.getMessage(), e);
            }
        }
    }
}
