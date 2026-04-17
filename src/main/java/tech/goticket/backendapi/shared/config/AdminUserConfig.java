package tech.goticket.backendapi.shared.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.admin.Admin;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.user.repository.RoleRepository;
import tech.goticket.backendapi.user.repository.UserRepository;
import tech.goticket.backendapi.shared.model.status.StatusRepository;
import tech.goticket.backendapi.admin.AdminService;

import java.time.Instant;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private AdminService adminService;
    private StatusRepository statusRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminUserConfig(RoleRepository roleRepository,
                           AdminService adminService,
                           UserRepository userRepository,
                           StatusRepository statusRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.statusRepository = statusRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var adminStatus = statusRepository.findByName(Status.Values.ACTIVE.name());

        var userAdmin = userRepository.findByEmail("admin@admin.com");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("admin já existe");
                },
                () -> {
                    Admin newAdmin = new Admin(
                            "admin@admin.com",
                            bCryptPasswordEncoder.encode("123"),
                            roleAdmin,
                            adminStatus,
                            "Administrador GoTicket",
                            Instant.now(),
                            Instant.now()
                    );

                    adminService.saveAdmin(newAdmin);
                }
        );
    }
}
