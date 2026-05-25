package tech.goticket.backendapi.event.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.enums.EventStatus;
import tech.goticket.backendapi.event.enums.EventVisibility;
import tech.goticket.backendapi.event.dto.*;
import tech.goticket.backendapi.event.service.EventService;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final ObjectMapper objectMapper;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> createNewEvent(@Valid @RequestBody CreateEventDTO dto,
                                               Authentication authentication) {

        UUID loggedUserId = UUID.fromString(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SCOPE_ADMIN"));

        Event event = eventService.createEvent(dto, loggedUserId, isAdmin);

        return ResponseEntity.created(URI.create("/events/" + event.getEventId())).build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventPageDTO> findEventById(@PathVariable Long eventId,
                                                      Authentication authentication) {

        UUID userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId = UUID.fromString(authentication.getName());
        }

        EventPageDTO event = eventService.findByEventID(eventId, userId);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventId}/details")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<EventFullDTO> findEventDetailsById(@PathVariable Long eventId,
                                                      Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        EventFullDTO event = eventService.findByEventIDWithFullInfo(eventId, userId);

        return ResponseEntity.ok(event);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('SCOPE_ORGANIZER')")
    public ResponseEntity<OrganizerEventListDTO> listMyEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
            Authentication authentication) {

        UUID organizerId = UUID.fromString(authentication.getName());
        OrganizerEventListDTO events = eventService.findEventsByOrganizer(
                organizerId,
                PageRequest.of(page, pageSize, Sort.Direction.DESC, "registerDate"));

        return ResponseEntity.ok(events);
    }

    @GetMapping
    public ResponseEntity<EventMinListDTO> listApprovedPublicEvents(@RequestParam(name = "title", required = false) String title,
                                                                    @RequestParam(name = "categoryId", required = false) Long categoryId,
                                                                    @RequestParam(name = "startingPrice", required = false) Double startingPrice,
                                                                    @RequestParam(name = "venueState", required = false) String venueState,
                                                                    @RequestParam(name = "venueCity", required = false) String venueCity,
                                                                    @RequestParam(name = "page",defaultValue = "0") int page,
                                                                    @RequestParam(name = "pageSize",defaultValue = "10") int pageSize){

        var events = eventService.findApprovedPublicEvents(title,
                categoryId,
                startingPrice,
                venueState,
                venueCity,
                PageRequest.of(page,pageSize, Sort.Direction.ASC, "startDate"));

        return ResponseEntity.ok(events);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EventMinListDTO> listAllEvents(@RequestParam(name = "title", required = false) String title,
                                                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                                                         @RequestParam(name = "statusId", required = false) Long statusId,
                                                         @RequestParam(name = "venueState", required = false) String venueState,
                                                         @RequestParam(name = "venueCity", required = false) String venueCity,
                                                         @RequestParam(name = "page",defaultValue = "0") int page,
                                                         @RequestParam(name = "pageSize",defaultValue = "10") int pageSize) {
        var events = eventService.findAllEvents(title,
                categoryId,
                statusId,
                venueState,
                venueCity,
                PageRequest.of(page,pageSize, Sort.Direction.ASC, "startDate"));

        return ResponseEntity.ok(events);
    }

    @PatchMapping(value = "/{eventId}", consumes = "application/merge-patch+json")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<EventFullDTO> updateEvent(@PathVariable Long eventId,
                                             @RequestBody JsonNode patchNode,
                                             Authentication authentication){
        UUID userId = UUID.fromString(authentication.getName());
        EventFullDTO event = eventService.updateEvent(eventId, patchNode, userId);

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

    @PatchMapping("/{eventId}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> updateEventStatus(
            @PathVariable Long eventId,
            @RequestBody Map<String, EventStatus.Values> payload) {

        var newStatus = payload.get("status");

        if (newStatus == null) {
            throw new InvalidArgumentException("O campo 'status' é obrigatório.");
        }

        eventService.updateStatus(eventId, newStatus);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{eventId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> replaceEventImages(
            @PathVariable Long eventId,
            @RequestParam("metadata") String metadataJson,
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        // Desserializa o JSON de metadata
        List<EventImageOrderItemDTO> metadata;
        try {
            metadata = objectMapper.readValue(
                    metadataJson,
                    new TypeReference<>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new InvalidArgumentException("JSON de metadata inválido.");
        }

        eventService.replaceImages(eventId, metadata, newImages, userId);

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

    @DeleteMapping("/{eventId}/images/{imageKey}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<String> deleteEventImageByKey(@PathVariable(name = "eventId") Long eventId,
                                                @PathVariable(name = "imageKey") String imageKey,
                                                Authentication authentication){
        var userId = authentication.getName();
        UUID uuid = UUID.fromString(userId);

        eventService.deleteEventImageByKey(eventId, imageKey, uuid);
        return ResponseEntity.ok(imageKey);
    }
}