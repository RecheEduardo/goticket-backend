package tech.goticket.backendapi.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.EventDateSector;
import tech.goticket.backendapi.event.EventSector;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.shared.exception.ConflictException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventDateSectorService {

    private final EventRepository eventRepository;
    private final EventAuthorizationService eventAuthorizationService;

    @Transactional
    public EventDateSector link(Long eventId, Long eventDateId, Long eventSectorId, UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        EventDate eventDate = event.getEventDates().stream()
                .filter(d -> d.getEventDateId().equals(eventDateId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Data do evento não encontrada."));

        EventSector eventSector = event.getSectors().stream()
                .filter(s -> s.getSectorId().equals(eventSectorId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Setor do evento não encontrado."));

        boolean alreadyLinked = eventDate.getDateSectors().stream()
                .anyMatch(eds -> eds.getEventSector().getSectorId().equals(eventSectorId));

        if (alreadyLinked) {
            throw new ConflictException("Este setor já está vinculado a esta data do evento.");
        }

        Instant now = Instant.now();
        EventDateSector eds = new EventDateSector();
        eds.setEventDate(eventDate);
        eds.setEventSector(eventSector);
        eds.setRegisterDate(now);
        eds.setLastUpdateDate(now);

        eventDate.getDateSectors().add(eds);
        event.setLastUpdateDate(now);

        eventRepository.save(event);
        return eds;
    }

    @Transactional
    public void unlink(Long eventId, Long eventDateId, Long eventDateSectorId, UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        EventDate eventDate = event.getEventDates().stream()
                .filter(d -> d.getEventDateId().equals(eventDateId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Data do evento não encontrada."));

        EventDateSector target = eventDate.getDateSectors().stream()
                .filter(eds -> eds.getEventDateSectorId().equals(eventDateSectorId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Vínculo setor-data não encontrado."));

        if (target.getSoldTickets() > 0) {
            throw new ConflictException("Não é possível remover um setor com ingressos vendidos.");
        }

        eventDate.getDateSectors().remove(target);
        event.setLastUpdateDate(Instant.now());

        eventRepository.save(event);
    }

    private Event loadAndAuthorize(Long eventId, UUID userId) {
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));
        eventAuthorizationService.requireOwnerOrAdmin(event, userId,
                "Usuário não tem permissão para alterar este evento.");
        return event;
    }
}
