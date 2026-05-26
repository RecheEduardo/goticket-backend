package tech.goticket.backendapi.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.ticket.dto.TicketResponse;
import tech.goticket.backendapi.ticket.service.TicketService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_CLIENT', 'SCOPE_ADMIN')")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID ticketId,
                                                  Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ticketService.findByIdForUser(ticketId, requesterId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<List<TicketResponse>> getMine(Authentication authentication) {
        UUID buyerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ticketService.findMyTickets(buyerId));
    }
}
