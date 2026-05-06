package tech.goticket.backendapi.event.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.event.EventStatus;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.event.repository.EventStatusRepository;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EventDateService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    private EventService eventService;

    @Transactional
    public EventDate createEventDate(Long eventId, LocalDateTime start, LocalDateTime end, UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        validateRange(start, end);

        EventStatus approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());

        Instant now = Instant.now();
        EventDate newDate = new EventDate();
        newDate.setEvent(event);
        newDate.setStartDate(start);
        newDate.setEndDate(end);
        newDate.setStatus(approvedStatus);
        newDate.setRegisterDate(now);
        newDate.setLastUpdateDate(now);

        event.getEventDates().add(newDate);
        event.recalculateDateRange();

        eventRepository.save(event);
        return newDate;
    }

    @Transactional
    public EventDate updateEventDate(Long eventId, Long eventDateId,
                                     LocalDateTime newStart, LocalDateTime newEnd,
                                     UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        EventDate target = event.getEventDates().stream()
                .filter(d -> d.getEventDateId().equals(eventDateId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Data do evento não encontrada."));

        validateRange(newStart, newEnd);

        target.setStartDate(newStart);
        target.setEndDate(newEnd);
        target.setLastUpdateDate(Instant.now());

        event.recalculateDateRange();

        eventRepository.save(event);
        return target;
    }

    @Transactional
    public void deleteEventDate(Long eventId, Long eventDateId, UUID userId) {
        Event event = loadAndAuthorize(eventId, userId);

        boolean removed = event.getEventDates()
                .removeIf(d -> d.getEventDateId().equals(eventDateId));

        if (!removed) {
            throw new ResourceNotFoundException("Data do evento não encontrada.");
        }

        if (event.getEventDates().isEmpty()) {
            throw new InvalidArgumentException("Um evento deve ter pelo menos uma data.");
        }

        event.recalculateDateRange();

        eventRepository.save(event);
    }

    private Event loadAndAuthorize(Long eventId, UUID userId) {
        Event event = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));
        eventService.validateUserPermission(event, userId,
                "Usuário não tem permissão para alterar as datas deste evento.");
        return event;
    }

    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new InvalidArgumentException("startDate deve ser anterior a endDate.");
        }
    }
}