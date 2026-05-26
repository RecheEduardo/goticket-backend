package tech.goticket.backendapi.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventSector;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.venue.VenueSector;
import tech.goticket.backendapi.venue.VenueSectorRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventSectorService {

    private final EventRepository eventRepository;

    private final VenueSectorRepository venueSectorRepository;

    private final EventAuthorizationService eventAuthorizationService;

    @Transactional
    public EventSector createEventSector(Long eventId,
                                         String name,
                                         String description,
                                         boolean hasNumberedSeats,
                                         Long venueSectorId,
                                         UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        VenueSector venueSector = venueSectorRepository
                .findBySectorIdAndVenue_VenueId(venueSectorId, event.getVenue().getVenueId())
                .orElseThrow(() -> new InvalidArgumentException(
                        "O setor informado não pertence ao espaço deste evento."
                ));

        boolean alreadyLinked = event.getSectors().stream()
                .anyMatch(s -> s.getVenueSector() != null
                        && venueSectorId.equals(s.getVenueSector().getSectorId()));

        if (alreadyLinked) {
            throw new InvalidArgumentException(
                    "Já existe um setor do evento associado a este setor do espaço."
            );
        }

        Instant now = Instant.now();
        EventSector eventSector = new EventSector();
        eventSector.setName(name);
        eventSector.setDescription(description);
        eventSector.setHasNumberedSeats(hasNumberedSeats);
        eventSector.setVenueSector(venueSector);
        eventSector.setEvent(event);
        eventSector.setRegisterDate(now);
        eventSector.setLastUpdateDate(now);

        event.getSectors().add(eventSector);
        event.setLastUpdateDate(now);

        eventRepository.save(event);
        return eventSector;
    }

    @Transactional
    public EventSector updateEventSector(Long eventId,
                                         Long sectorId,
                                         String name,
                                         String description,
                                         Boolean hasNumberedSeats,
                                         UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        EventSector target = event.getSectors().stream()
                .filter(s -> s.getSectorId().equals(sectorId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Setor do evento não encontrado."));

        boolean touched = false;

        if (name != null) {
            if (name.isBlank()) {
                throw new InvalidArgumentException("name não pode ser vazio.");
            }
            target.setName(name);
            touched = true;
        }
        if (description != null) {
            if (description.isBlank()) {
                throw new InvalidArgumentException("description não pode ser vazio.");
            }
            target.setDescription(description);
            touched = true;
        }
        if (hasNumberedSeats != null) {
            target.setHasNumberedSeats(hasNumberedSeats);
            touched = true;
        }

        if (!touched) {
            throw new InvalidArgumentException("Nenhum campo informado para atualização.");
        }

        Instant now = Instant.now();
        target.setLastUpdateDate(now);
        event.setLastUpdateDate(now);

        eventRepository.save(event);
        return target;
    }

    @Transactional
    public void deleteEventSector(Long eventId, Long sectorId, UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        EventSector target = event.getSectors().stream()
                .filter(s -> s.getSectorId().equals(sectorId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Setor do evento não encontrado."));

        if (target.getDateSectors() != null && !target.getDateSectors().isEmpty()) {
            throw new InvalidArgumentException(
                    "Não é possível remover um setor que já está vinculado a datas do evento."
            );
        }

        event.getSectors().remove(target);
        event.setLastUpdateDate(Instant.now());

        eventRepository.save(event);
    }

    private Event loadAndAuthorize(Long eventId, UUID userId) {
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));
        eventAuthorizationService.requireOwnerOrAdmin(event, userId,
                "Usuário não tem permissão para alterar os setores deste evento.");
        return event;
    }
}
