package tech.goticket.backendapi.services;

import org.springframework.stereotype.Service;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.repository.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveEvent(Event event) { eventRepository.save(event); }
}
