package tech.goticket.backendapi.demand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.demand.dto.SetDemandTierRequest;
import tech.goticket.backendapi.demand.service.DemandOverrideService;

import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/demand-tier")
@RequiredArgsConstructor
public class DemandController {

    private final DemandOverrideService demandOverrideService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> setTier(@PathVariable Long eventId,
                                        @Valid @RequestBody SetDemandTierRequest body,
                                        Authentication authentication) {
        UUID requesterId = UUID.fromString(authentication.getName());
        demandOverrideService.override(eventId, body.tier(), body.validForMinutes(), requesterId);
        return ResponseEntity.noContent().build();
    }
}
