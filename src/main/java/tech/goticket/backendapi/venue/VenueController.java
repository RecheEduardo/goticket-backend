package tech.goticket.backendapi.venue;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.organizer.Organizer;
import tech.goticket.backendapi.organizer.OrganizerService;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.user.DocumentAlreadyExistsException;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.shared.model.status.StatusRepository;
import tech.goticket.backendapi.shared.utils.DocumentValidator;
import tech.goticket.backendapi.venue.dto.CreateVenueDTO;
import tech.goticket.backendapi.venue.dto.VenueListDTO;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private StatusRepository statusRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<VenueListDTO> listVenues(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        var venues = venueService.findAll(
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "name"));

        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> findVenueById(@PathVariable Long venueId) {
        Venue venue = venueService.findById(venueId)
                .orElseThrow(() -> { return new ResourceNotFoundException("Espaço não encontrado."); });

        return ResponseEntity.ok(venue);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Void> createNewVenue(@Valid @RequestBody CreateVenueDTO dto, Authentication authentication) {
        boolean isCNPJ = DocumentValidator.isCNPJ(dto.CNPJ());
        if(!isCNPJ) { throw new InvalidArgumentException("CNPJ informado é inválido."); }

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

        venueService.findByCNPJ(dto.CNPJ())
                .ifPresent(venue -> { throw new DocumentAlreadyExistsException("Este CNPJ já está cadastrado no sistema para outro espaço."); });

        Instant now = Instant.now();
        Status venueStatus = statusRepository.findByName(Status.Values.ACTIVE.name());

        Venue newVenue = new Venue(
                dto.name(),
                dto.legalName(),
                dto.CNPJ(),
                dto.description(),
                dto.streetAddress(),
                dto.streetAddressNumber(),
                dto.neighborhood(),
                dto.city(),
                dto.state(),
                dto.country(),
                now,
                now,
                venueStatus,
                organizer
        );
        newVenue.setZipCode(dto.zipCode());

        venueService.saveVenue(newVenue);

        return ResponseEntity.created(URI.create("/venues/" + newVenue.getVenueID())).build();
    }

    @PatchMapping(value = "/{venueId}", consumes = "application/merge-patch+json")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long venueId,
                                             @RequestBody JsonNode patchNode,
                                             Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        Venue venue = venueService.updateVenue(venueId, patchNode, userId);

        return ResponseEntity.ok(venue);
    }

    @DeleteMapping("/{venueId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")
    public ResponseEntity<Long> deleteVenueById(@PathVariable Long venueId,
                                                Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        venueService.deleteVenue(venueId, userId);

        return ResponseEntity.ok(venueId);
    }
}
