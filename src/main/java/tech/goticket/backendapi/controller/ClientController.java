package tech.goticket.backendapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateClientDTO;
import tech.goticket.backendapi.entities.Client;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.repository.ClientRepository;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.ClientService;

import java.time.Instant;
import java.time.LocalDate;

@RestController
public class ClientController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClientService clientService;
    private final UserStatusRepository userStatusRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClientController(UserRepository userRepository,
                            RoleRepository roleRepository,
                            ClientService clientService,
                            UserStatusRepository userStatusRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.clientService = clientService;
        this.userStatusRepository = userStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/clients")
    @Transactional
    public ResponseEntity<Void> createNewClient(@RequestBody CreateClientDTO dto) {

        boolean isCpf = ClientService.isCPF(dto.identityDocument());
        if (!isCpf) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }

        var userFromDb = userRepository.findByEmail(dto.email());
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

        return ResponseEntity.ok().build();
    }
}
