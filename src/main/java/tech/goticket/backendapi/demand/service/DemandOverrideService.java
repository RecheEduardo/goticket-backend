package tech.goticket.backendapi.demand.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.demand.EventDemandProfile;
import tech.goticket.backendapi.demand.repository.EventDemandProfileRepository;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.event.service.EventAuthorizationService;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemandOverrideService {
    private static final String TIER_KEY = "demand:event:%d:tier";

    private final EventRepository eventRepository;
    private final EventDemandProfileRepository profileRepository;
    private final EventAuthorizationService eventAuthorizationService;
    private final StringRedisTemplate redis;

    @Value("${goticket.demand.manual-default-minutes:120}")
    private int defaultMinutes;

    @Transactional
    public void override(Long eventId, String tier, Integer validForMinutes, UUID requesterId) {
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));
        eventAuthorizationService.requireOwnerOrAdmin(event, requesterId,"Sem permissão para alterar a demanda deste evento.");

        int minutes = (validForMinutes != null) ? validForMinutes : defaultMinutes;
        Instant until = Instant.now().plus(Duration.ofMinutes(minutes));

        EventDemandProfile profile = profileRepository.findByEventId(eventId)
                .orElseGet(() -> { var p = new EventDemandProfile(); p.setEventId(eventId); return p; });
        profile.setTier(tier);
        profile.setSource("MANUAL");
        profile.setManualOverrideUntil(until);
        profile.setLastEvaluatedAt(Instant.now());
        profileRepository.save(profile);

        String key = String.format(TIER_KEY, eventId);
        if ("HIGH".equals(tier)) {
            redis.opsForValue().set(key, "HIGH", Duration.ofMinutes(minutes));
        } else {
            redis.delete(key);
        }
    }
}
