package tech.goticket.backendapi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateEventDTO;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.repository.EventRepository;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.EventService;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
public class EventController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping("/events")
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
    public ResponseEntity<Void> createNewEvent(@RequestBody CreateEventDTO dto) {

        User organizer = userRepository.findById(dto.organizerID())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizador não encontrado"));

        Role userRole = organizer.getRole();
        boolean isOrganizer = userRole.getName().equals(Role.Values.ORGANIZER.name());

        if(!isOrganizer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Usuário não tem permissão para executar esta ação"
            );
        }

        var eventFromDb = eventRepository.findByEventID(dto.eventID());

        if(eventFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var now = Instant.now();

        var event = new Event();
        event.setEventID(dto.eventID());
        event.setOrganizerID(organizer);
        event.setEventTitle(dto.eventTitle());
        event.setEventDescription(dto.eventDescription());
        event.setCreationTimeStamp(now);

        eventService.saveEvent(event);

        return ResponseEntity.created(URI.create("/events/" + event.getEventID())).build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> listAllEvents(){
        var events = eventRepository.findAll();

        return ResponseEntity.ok(events);
    }
}