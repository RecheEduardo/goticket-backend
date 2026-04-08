package tech.goticket.backendapi.event;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.event.dto.CreateEventDTO;
import tech.goticket.backendapi.event.dto.EventMinListDTO;
import tech.goticket.backendapi.event.repository.EventRepository;
import tech.goticket.backendapi.event.repository.EventStatusRepository;
import tech.goticket.backendapi.event.repository.EventVisibilityRepository;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.organizer.OrganizerService;
import tech.goticket.backendapi.user.UserService;
import tech.goticket.backendapi.user.repository.RoleRepository;
import tech.goticket.backendapi.user.repository.UserStatusRepository;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
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
    private EventVisibilityRepository eventVisibilityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping
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
                .orElseThrow(() -> new ResourceNotFoundException("Organizador informado não encontrado."));

        // Review status when approval endpoint for admins get implemented
        EventStatus approvedStatus = eventStatusRepository.findByName(EventStatus.Values.APPROVED.name());

        EventVisibility privateVisibility = eventVisibilityRepository.findByName(EventVisibility.Values.PRIVATE.name())
                .orElseThrow(() -> new ResourceNotFoundException("Visibilidade de evento não encontrada."));

        Instant now = Instant.now();

        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setAgeRestriction(dto.ageRestriction());
        event.setEventVisibility(privateVisibility);
        event.setStartDate(dto.startDate());
        event.setEndDate(dto.endDate());
        event.setRegisterDate(now);
        event.setLastUpdateDate(now);
        event.setStatus(approvedStatus);
        event.setOrganizer(organizer);

        if (dto.salesStartDate() != null) { event.setSalesStartDate(dto.salesStartDate()); }

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
    public ResponseEntity<EventMinListDTO> listApprovedPublicEvents(@RequestParam(name = "page",defaultValue = "0") int page,
                                                                    @RequestParam(name = "pageSize",defaultValue = "10") int pageSize){
        var events = eventService.findApprovedPublicEvents(PageRequest.of(page,pageSize, Sort.Direction.ASC, "startDate"));

        return ResponseEntity.ok(events);
    }

    @PatchMapping(value = "/{eventId}", consumes = "application/merge-patch+json")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId,
                                             @RequestBody JsonNode patchNode,
                                             Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());
        Event event = eventService.updateEvent(eventId, patchNode, userId);

        return ResponseEntity.ok(event);
    }

    @PatchMapping("/{eventId}/visibility")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> updateEventVisibility(
            @PathVariable Long eventId,
            @RequestBody Map<String, EventVisibility.Values> payload,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        var newVisibility = payload.get("visibility");

        if (newVisibility == null) {
            throw new InvalidArgumentException("O campo 'visibility' é obrigatório.");
        }

        eventService.updateVisibility(eventId, newVisibility, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Long> deleteEventByID(@PathVariable(name = "eventId") Long eventId,
                                                 Authentication authentication){
        var userId = authentication.getName();
        UUID uuid = UUID.fromString(userId);

        eventService.deleteEventById(eventId, uuid);
        return ResponseEntity.ok(eventId);
    }
}