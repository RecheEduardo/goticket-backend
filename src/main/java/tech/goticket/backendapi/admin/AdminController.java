package tech.goticket.backendapi.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.shared.exception.ResourceNotFoundException;

import java.util.UUID;

@RestController
@RequestMapping(value = "/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/{adminId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Admin> getAdminById(@PathVariable String adminId) {
        UUID uuid = UUID.fromString(adminId);
        Admin admin = this.adminService.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário administrador com este ID não encontrado."));

        return ResponseEntity.ok(admin);
    }
}
