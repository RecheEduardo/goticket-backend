package tech.goticket.backendapi.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.controller.dto.EventMinDTO;
import tech.goticket.backendapi.controller.dto.EventMinListDTO;
import tech.goticket.backendapi.controller.projections.EventMinProjection;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.entities.EventStatus;
import tech.goticket.backendapi.repository.EventRepository;
import tech.goticket.backendapi.repository.EventStatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    public Optional<Event> findByEventID(Long eventID) { return eventRepository.findByEventID(eventID); }

    @Transactional
    public EventMinListDTO findApprovedEvents(PageRequest pageRequest) {
        var approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());
        var events = eventRepository.findAllEventsByStatus(approvedStatus, pageRequest)
                .map(EventMinDTO::new);

        return new EventMinListDTO(pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                events.getTotalPages(),
                events.getTotalElements(),
                events.toList());
    }

    public void saveEvent(Event event) { eventRepository.save(event); }

    public void deleteEvent(Event event) { eventRepository.delete(event); }
}
