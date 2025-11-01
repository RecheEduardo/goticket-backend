package tech.goticket.backendapi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateEventDTO;
import tech.goticket.backendapi.controller.dto.EventMinListDTO;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.entities.EventStatus;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.repository.EventRepository;
import tech.goticket.backendapi.repository.EventStatusRepository;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.EventService;
import tech.goticket.backendapi.services.UserService;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private UserService userService;

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

        User organizer = userService.findById(dto.organizerID())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organizador não encontrado"));

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
        event.setOrganizerID(organizer);

        eventService.saveEvent(event);

        return ResponseEntity.created(URI.create("/events/" + event.getEventID())).build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> findEventById(@PathVariable Long eventId) {
        var event = eventService.findByEventID(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento não encontrado."));

        return ResponseEntity.ok(event);
    }

    @GetMapping
    public ResponseEntity<EventMinListDTO> listApprovedEvents(@RequestParam(name = "page",defaultValue = "0") int page,
                                                              @RequestParam(name = "page",defaultValue = "10") int pageSize){
        var events = eventService.findApprovedEvents(PageRequest.of(page,pageSize, Sort.Direction.ASC, "startDate"));

        return ResponseEntity.ok(events);
    }

    @DeleteMapping
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
    public ResponseEntity<Event> deleteEventByID(@RequestParam(name = "eventID") Long eventID,
                                                 @RequestBody CreateEventDTO dto){

        Event targetEvent = eventService.findByEventID(eventID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento não encontrado"));

        User organizer = userService.findById(dto.organizerID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organizador não encontrado"));

        User EventOrganizer = targetEvent.getOrganizerID();

        boolean isOrganizer = EventOrganizer.getUserID().equals(organizer.getUserID());

        if(!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Usuário não tem permissão para executar esta ação"
            );
        }

        eventService.deleteEvent(targetEvent);

        return ResponseEntity.ok(targetEvent);
    }
}