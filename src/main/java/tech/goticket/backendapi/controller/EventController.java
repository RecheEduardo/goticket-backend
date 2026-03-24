package tech.goticket.backendapi.controller;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateEventDTO;
import tech.goticket.backendapi.controller.dto.EventMinListDTO;
import tech.goticket.backendapi.entities.*;
import tech.goticket.backendapi.exceptions.ResourceNotFoundException;
import tech.goticket.backendapi.repository.EventRepository;
import tech.goticket.backendapi.repository.EventStatusRepository;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.EventService;
import tech.goticket.backendapi.services.OrganizerService;
import tech.goticket.backendapi.services.UserService;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
    public ResponseEntity<Void> createNewEvent(@RequestBody CreateEventDTO dto) {

        Organizer organizer = organizerService.findById(dto.organizerID())
        .orElseThrow(() -> new ResourceNotFoundException("Organizador informado não encontrado"));

        Role userRole = organizer.getRole();
        boolean isOrganizer = userRole.getName().equals(Role.Values.ORGANIZER.name());

        if(!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Usuário não tem permissão para executar esta ação"
            );
        }

        var eventFromDb = eventService.findByEventID(dto.eventID());

        if(eventFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // Review status when approval endpoint for admins get implemented
        var approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());

        var now = Instant.now();

        var event = new Event();
        event.setEventID(dto.eventID());
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setAgeRestriction(dto.ageRestriction());
        event.setStartDate(dto.startDate());
        event.setEndDate(dto.endDate());
        event.setRegisterDate(now);
        event.setLastUpdateDate(now);
        event.setStatus(approvedStatus);
        event.setOrganizer(organizer);

        eventService.saveEvent(event);

        return ResponseEntity.created(URI.create("/events/" + event.getEventID())).build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> findEventById(@PathVariable Long eventId) {
        var event = eventService.findByEventID(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado."));

        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<EventMinListDTO> listApprovedEvents(@RequestParam(name = "page",defaultValue = "0") int page,
                                                              @RequestParam(name = "pageSize",defaultValue = "10") int pageSize){
        var events = eventService.findApprovedEvents(PageRequest.of(page,pageSize, Sort.Direction.ASC, "startDate"));

        return ResponseEntity.ok(events);
    }

    @PatchMapping(value = "/{eventId}", consumes = "application/merge-patch+json")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId,
                                             @RequestBody JsonNode patchNode,
                                             Authentication authentication){
        var userId = authentication.getName();
        UUID uuid = UUID.fromString(userId);
        var event = eventService.updateEvent(eventId, patchNode, uuid);

        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{eventId}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Long> deleteEventByID(@PathVariable(name = "eventId") Long eventId,
                                                 Authentication authentication){
        var userId = authentication.getName();
        UUID uuid = UUID.fromString(userId);

        eventService.deleteEventById(eventId, uuid);
        return ResponseEntity.ok(eventId);
    }
}