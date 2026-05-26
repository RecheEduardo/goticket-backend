package tech.goticket.backendapi.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.event.EventDateSector;
import tech.goticket.backendapi.event.dto.CreateEventDateSectorDTO;
import tech.goticket.backendapi.event.dto.EventFullDTO;
import tech.goticket.backendapi.event.service.EventDateSectorService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/dates/{eventDateId}/sectors")
@RequiredArgsConstructor
public class EventDateSectorController {

    private final EventDateSectorService eventDateSectorService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<EventFullDTO.EventDateSectorFullDTO> linkSector(
            @PathVariable Long eventId,
            @PathVariable Long eventDateId,
            @Valid @RequestBody CreateEventDateSectorDTO dto,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        EventDateSector created = eventDateSectorService.link(eventId, eventDateId, dto.eventSectorId(), userId);
        return ResponseEntity
                .created(URI.create("/events/" + eventId + "/dates/" + eventDateId
                        + "/sectors/" + created.getEventDateSectorId()))
                .body(new EventFullDTO.EventDateSectorFullDTO(created));
    }

    @DeleteMapping("/{eventDateSectorId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> unlinkSector(
            @PathVariable Long eventId,
            @PathVariable Long eventDateId,
            @PathVariable Long eventDateSectorId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        eventDateSectorService.unlink(eventId, eventDateId, eventDateSectorId, userId);
        return ResponseEntity.noContent().build();
    }
}
