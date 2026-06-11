package tech.goticket.backendapi.waitingroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.waitingroom.dto.QueueStatusResponse;
import tech.goticket.backendapi.waitingroom.service.WaitingRoomService;

import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/queue")
@RequiredArgsConstructor
public class WaitingRoomController {

    private final WaitingRoomService waitingRoomService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<QueueStatusResponse> enterQueue(@PathVariable Long eventId,
                                                          Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(waitingRoomService.enqueue(eventId, userId));
    }

    @GetMapping("/position")
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<QueueStatusResponse> position(
            @PathVariable Long eventId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        QueueStatusResponse status = waitingRoomService.getStatus(eventId, userId);

        if (status == null) {
            status = waitingRoomService.enqueue(eventId, userId);
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(status);
    }
}
