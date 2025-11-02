package tech.goticket.backendapi.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.EventMinDTO;
import tech.goticket.backendapi.controller.dto.EventMinListDTO;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.entities.EventStatus;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.repository.EventRepository;
import tech.goticket.backendapi.repository.EventStatusRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

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

    public Event updateEvent(Long eventId, JsonNode patchNode, UUID userId) {
        Event existingEvent = eventRepository.findByEventID(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento não encontrado"));

        var requestUser = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um erro ocorreu na sessão atual, faça login novamente."));

        boolean isAdmin = requestUser.getRole().getName().equals(Role.Values.ADMIN.name());
        boolean isEventOwner = requestUser.getUserID().equals(existingEvent.getOrganizerID().getUserID());

        if(!isAdmin && !isEventOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não tem permissão para executar esta ação.");
        }

        try {
            JsonNode existingEventNode = objectMapper.valueToTree(existingEvent);

            JsonNode patchedNode = objectMapper.readerForUpdating(existingEventNode)
                    .readValue(patchNode);

            Event updatedEvent = objectMapper.treeToValue(patchedNode, Event.class);

            return eventRepository.save(updatedEvent);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar evento: " + e.getMessage());
        }
    }
}
