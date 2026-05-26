package tech.goticket.backendapi.ticket.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.ticket.dto.EligibilityTypeDTO;
import tech.goticket.backendapi.ticket.repository.EligibilityTypeRepository;

import java.util.List;

@RestController
@RequestMapping("/eligibility-types")
@RequiredArgsConstructor
public class EligibilityTypeController {
    private final EligibilityTypeRepository repository;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_CLIENT')")
    public ResponseEntity<List<EligibilityTypeDTO>> list() {
        List<EligibilityTypeDTO> result = repository.findAll().stream()
                .map(EligibilityTypeDTO::from)
                .toList();
        return ResponseEntity.ok(result);
    }
}
