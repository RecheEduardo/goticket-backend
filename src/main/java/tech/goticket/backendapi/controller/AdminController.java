package tech.goticket.backendapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.entities.Admin;
import tech.goticket.backendapi.exceptions.ResourceNotFoundException;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.AdminService;

import java.util.UUID;

@RestController
@RequestMapping(value = "/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @GetMapping("/{adminId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Admin> getAdminById(@PathVariable String adminId) {
        UUID uuid = UUID.fromString(adminId);
        Admin admin = this.adminService.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário administrador com este ID não encontrado."));

        return ResponseEntity.ok(admin);
    }
}
