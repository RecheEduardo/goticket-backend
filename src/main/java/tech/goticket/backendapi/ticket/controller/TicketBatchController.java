package tech.goticket.backendapi.ticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.event.dto.EventFullDTO;
import tech.goticket.backendapi.ticket.TicketBatch;
import tech.goticket.backendapi.ticket.dto.CreateTicketBatchDTO;
import tech.goticket.backendapi.ticket.service.TicketBatchService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TicketBatchController {

    private final TicketBatchService ticketBatchService;

    @PostMapping("/events/{eventId}/date-sectors/{eventDateSectorId}/batches")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<EventFullDTO.TicketBatchFullDTO> createBatch(
            @PathVariable Long eventId,
            @PathVariable Long eventDateSectorId,
            @Valid @RequestBody CreateTicketBatchDTO dto,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        TicketBatch created = ticketBatchService.createBatch(eventId, eventDateSectorId, dto, userId);
        return ResponseEntity
                .created(URI.create("/events/" + eventId + "/batches/" + created.getBatchId()))
                .body(new EventFullDTO.TicketBatchFullDTO(created));
    }

    @DeleteMapping("/events/{eventId}/batches/{batchId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> deleteBatch(
            @PathVariable Long eventId,
            @PathVariable Long batchId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ticketBatchService.deleteBatch(eventId, batchId, userId);
        return ResponseEntity.noContent().build();
    }
}
