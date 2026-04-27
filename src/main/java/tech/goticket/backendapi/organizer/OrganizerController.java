package tech.goticket.backendapi.organizer;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import tech.goticket.backendapi.organizer.dto.CreateOrganizerDTO;
import tech.goticket.backendapi.organizer.dto.OrganizerListDTO;
import tech.goticket.backendapi.user.dto.LoginResponse;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.user.DocumentAlreadyExistsException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;
import tech.goticket.backendapi.user.repository.RoleRepository;
import tech.goticket.backendapi.shared.model.status.StatusRepository;
import tech.goticket.backendapi.user.UserService;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import tech.goticket.backendapi.shared.utils.DocumentValidator;
import tech.goticket.backendapi.user.token.AuthTokenService;

@RestController
@RequestMapping(value = "/organizers")
public class OrganizerController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping
    public ResponseEntity<LoginResponse> createNewOrganizer(@Valid @RequestBody CreateOrganizerDTO dto) {
        boolean isCNPJ = DocumentValidator.isCNPJ(dto.CNPJ());
        if (!isCNPJ) { throw new InvalidArgumentException("CNPJ informado é inválido."); }

        userService.findByEmail(dto.email())
                .ifPresent(user -> { throw new EmailAlreadyExistsException("Este e-mail já está cadastrado."); });

        organizerService.findByCNPJ(dto.CNPJ())
                .ifPresent(organizer -> { throw new DocumentAlreadyExistsException("Este CNPJ já está cadastrado."); });

        Role orgazinerRole = roleRepository.findByName(Role.Values.ORGANIZER.name());
        Status organizerStatus = statusRepository.findByName(Status.Values.ACTIVE.name());
        Instant now = Instant.now();

        Organizer organizer = new Organizer();
        organizer.setEmail(dto.email());
        organizer.setPassword(passwordEncoder.encode(dto.password()));
        organizer.setRole(orgazinerRole);
        organizer.setStatus(organizerStatus);
        organizer.setOrganizerName(dto.organizerName());
        organizer.setLegalName(dto.legalName());
        organizer.setCNPJ(dto.CNPJ());
        organizer.setRegisterDate(now);
        organizer.setLastUpdateDate(now);

        organizerService.saveOrganizer(organizer);

        return ResponseEntity.created(URI.create("/organizers/" + organizer.getUserID()))
                .body(authTokenService.issueTokens(organizer));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<OrganizerListDTO> listOrganizers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        OrganizerListDTO organizers = organizerService.findAll(
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "organizerName"));

        return ResponseEntity.ok(organizers);
    }

    @GetMapping("/{organizerId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #organizerId")
    public ResponseEntity<Organizer> getOrganizerById(@PathVariable String organizerId) {
        UUID uuid = UUID.fromString(organizerId);
        Organizer organizer = this.organizerService.findById(uuid).
                orElseThrow(() -> new ResourceNotFoundException("Usuário organizador não encontrado."));

        return ResponseEntity.ok(organizer);
    }

    @PatchMapping("/{organizerId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #organizerId")
    public ResponseEntity<Organizer> updateOrganizer(@PathVariable String organizerId,
                                                     @RequestBody JsonNode patchNode) {
        UUID uuid = UUID.fromString(organizerId);
        Organizer updatedOrganizer = this.organizerService.updateOrganizer(uuid, patchNode);

        return ResponseEntity.ok(updatedOrganizer);
    }
}