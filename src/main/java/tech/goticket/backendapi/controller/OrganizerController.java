package tech.goticket.backendapi.controller;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateOrganizerDTO;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.entities.Organizer;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.exceptions.InvalidArgumentException;
import tech.goticket.backendapi.exceptions.ResourceNotFoundException;
import tech.goticket.backendapi.exceptions.user.DocumentAlreadyExistsException;
import tech.goticket.backendapi.exceptions.user.EmailAlreadyExistsException;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.OrganizerService;
import tech.goticket.backendapi.services.UserService;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

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
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping
    public ResponseEntity<LoginResponse> createNewOrganizer(@Valid @RequestBody CreateOrganizerDTO dto) {
        boolean isCNPJ = OrganizerService.isCNPJ(dto.CNPJ());
        if (!isCNPJ) { throw new InvalidArgumentException("CNPJ informado é inválido."); }

        userService.findByEmail(dto.email())
                .ifPresent(user -> { throw new EmailAlreadyExistsException("Este e-mail já está cadastrado."); });

        organizerService.findByCNPJ(dto.CNPJ())
                .ifPresent(organizer -> { throw new DocumentAlreadyExistsException("Este CNPJ já está cadastrado."); });

        Role orgazinerRole = roleRepository.findByName(Role.Values.ORGANIZER.name());
        UserStatus organizerStatus = userStatusRepository.findByName(UserStatus.Values.ACTIVE.name());
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

        var expiresIn = 300L;

        var scope = organizer.getRole().getName();

        var claims = JwtClaimsSet.builder()
                .issuer("goticketbackend")
                .subject(organizer.getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scope)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.created(URI.create("/organizers/" + organizer.getUserID()))
                .body(new LoginResponse(jwtValue, expiresIn));
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