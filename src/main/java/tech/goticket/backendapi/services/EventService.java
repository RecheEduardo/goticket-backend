package tech.goticket.backendapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.repository.EventRepository;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Optional<Event> findByEventID(Long eventID) { return eventRepository.findByEventID(eventID); }

    public void saveEvent(Event event) { eventRepository.save(event); }

    public void deleteEvent(Event event) { eventRepository.delete(event); }
}
