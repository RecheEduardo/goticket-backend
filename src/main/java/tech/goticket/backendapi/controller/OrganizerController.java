package tech.goticket.backendapi.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.CreateOrganizerDTO;
import tech.goticket.backendapi.entities.Organizer;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.OrganizerService;

import java.time.Instant;

@RestController
public class OrganizerController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizerService organizerService;
    private final UserStatusRepository userStatusRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public OrganizerController(UserRepository userRepository, RoleRepository roleRepository, OrganizerService organizerService, UserStatusRepository userStatusRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizerService = organizerService;
        this.userStatusRepository = userStatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/organizers")
    @Transactional
    public ResponseEntity<Void> createNewOrganizer(@RequestBody CreateOrganizerDTO dto) {
        boolean isCNPJ = OrganizerService.isCNPJ(dto.CNPJ());
        if (!isCNPJ) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST); }

        var userFromDb = userRepository.findByEmail(dto.email());
        var organizerFromDb = organizerService.findByCNPJ(dto.CNPJ());

        if (userFromDb.isPresent() || organizerFromDb.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var orgazinerRole = roleRepository.findByName(Role.Values.ORGANIZER.name());
        var organizerStatus = userStatusRepository.findByName(UserStatus.Values.ACTIVE.name());
        var now = Instant.now();

        var organizer = new Organizer();
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

        return ResponseEntity.ok().build();
    }
}
