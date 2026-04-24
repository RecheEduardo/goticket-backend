package tech.goticket.backendapi.client;

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
import tech.goticket.backendapi.client.dto.ClientListDTO;
import tech.goticket.backendapi.client.dto.CreateClientDTO;
import tech.goticket.backendapi.shared.utils.DocumentValidator;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.dto.LoginResponse;
import tech.goticket.backendapi.shared.exception.InvalidArgumentException;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;
import tech.goticket.backendapi.shared.exception.user.DocumentAlreadyExistsException;
import tech.goticket.backendapi.shared.exception.user.EmailAlreadyExistsException;
import tech.goticket.backendapi.user.repository.RoleRepository;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.shared.model.status.StatusRepository;
import tech.goticket.backendapi.user.UserService;

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
    private StatusRepository statusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping
    public ResponseEntity<LoginResponse> createNewClient(@Valid @RequestBody CreateClientDTO dto) {

        boolean isCpf = DocumentValidator.isCPF(dto.identityDocument());
        if (!isCpf) { throw new InvalidArgumentException("CPF informado é inválido."); }

        userService.findByEmail(dto.email())
                .ifPresent(user -> { throw new EmailAlreadyExistsException("Este e-mail já está cadastrado."); });

        clientService.findByIdentityDocument(dto.identityDocument())
                .ifPresent(client -> { throw new DocumentAlreadyExistsException("Este CPF já está cadastrado."); });

        Role clientRole = roleRepository.findByName(Role.Values.CLIENT.name());
        Status clientStatus = statusRepository.findByName(Status.Values.ACTIVE.name());
        Instant now = Instant.now();

        var client = new Client(
                dto.email(),
                passwordEncoder.encode(dto.password()),
                clientRole,
                clientStatus,
                dto.fullName(),
                dto.sex(),
                dto.identityDocument(),
                LocalDate.parse(dto.birthDate()),
                now,
                now
        );

        clientService.saveClient(client);

        var scope = client.getRole().getName();

        var expiresIn = 900L;

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

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ClientListDTO> listClients(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        ClientListDTO clients = clientService.findAll(
                PageRequest.of(page, pageSize, Sort.Direction.ASC, "fullName"));

        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #clientId")
    public ResponseEntity<Client> getClientById(@PathVariable String clientId) {
        UUID uuid = UUID.fromString(clientId);
        Client client = this.clientService.findById(uuid).
                orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        return ResponseEntity.ok(client);
    }

    @PatchMapping("/{clientId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') || authentication.name == #clientId")
    public ResponseEntity<Client> updateClient(@PathVariable String clientId,
                                                     @RequestBody JsonNode patchNode) {
        UUID uuid = UUID.fromString(clientId);
        Client updatedClient = this.clientService.updateClient(uuid, patchNode);

        return ResponseEntity.ok(updatedClient);
    }
}
