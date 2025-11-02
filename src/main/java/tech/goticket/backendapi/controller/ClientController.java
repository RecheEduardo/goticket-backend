package tech.goticket.backendapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
import tech.goticket.backendapi.controller.dto.CreateClientDTO;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.entities.*;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.ClientService;
import tech.goticket.backendapi.services.UserService;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(value = "/clients")
public class ClientController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<LoginResponse> createNewClient(@RequestBody CreateClientDTO dto) {

        boolean isCpf = ClientService.isCPF(dto.identityDocument());
        if (!isCpf) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }

        var userFromDb = userService.findByEmail(dto.email());
        var clientFromDb = clientService.findByIdentityDocument(dto.identityDocument());

        if (userFromDb.isPresent() || clientFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var clientRole = roleRepository.findByName(Role.Values.CLIENT.name());
        var clientStatus = userStatusRepository.findByName(UserStatus.Values.ACTIVE.name());
        var now = Instant.now();

        var client = new Client();
        client.setEmail(dto.email());
        client.setPassword(passwordEncoder.encode(dto.password()));
        client.setRole(clientRole);
        client.setStatus(clientStatus);
        client.setFullName(dto.fullName());
        client.setSex(dto.sex());
        client.setIdentityDocument(dto.identityDocument());
        client.setBirthDate(LocalDate.parse(dto.birthDate()));
        client.setRegisterDate(now);
        client.setLastUpdateDate(now);

        clientService.saveClient(client);

        var scope = client.getRole().getName();

        var expiresIn = 300L;

        var claims = JwtClaimsSet.builder()
                .issuer("goticketbackend")
                .subject(client.getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope",scope)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.created(URI.create("/clients/" + client.getUserID()))
                .body(new LoginResponse(jwtValue, expiresIn));
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #clientId")
    public ResponseEntity<Client> getClientById(@PathVariable String clientId) {
        UUID uuid = UUID.fromString(clientId);
        var client = this.clientService.findById(uuid).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente não encontrado."));

        return ResponseEntity.ok(client);
    }

    @PatchMapping("/{clientId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #clientId")
    public ResponseEntity<Client> updateClient(@PathVariable String clientId,
                                                     @RequestBody JsonNode patchNode) {
        UUID uuid = UUID.fromString(clientId);
        var updatedClient = this.clientService.updateClient(uuid, patchNode);

        return ResponseEntity.ok(updatedClient);
    }
}
