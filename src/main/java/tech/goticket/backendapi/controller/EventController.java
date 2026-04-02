package tech.goticket.backendapi.controller;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
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
import tech.goticket.backendapi.exceptions.InvalidArgumentException;
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
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> createNewEvent(@Valid @RequestBody CreateEventDTO dto, Authentication authentication) {

        UUID loggedUserId = UUID.fromString(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"));

        UUID targetOrganizerId;

        if(isAdmin) {
            if(dto.organizerID() == null) {
                throw new InvalidArgumentException("O ID do organizador é obrigatório quando a criação é feita por um Administrador.");
            }
            targetOrganizerId = dto.organizerID();
        }
        else {
            targetOrganizerId = loggedUserId;
        }

        Organizer organizer = organizerService.findById(targetOrganizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador informado não encontrado"));

        // Review status when approval endpoint for admins get implemented
        EventStatus approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());

        Instant now = Instant.now();

        Event event = new Event();
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