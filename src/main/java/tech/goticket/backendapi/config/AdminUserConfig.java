package tech.goticket.backendapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tech.goticket.backendapi.entities.Admin;
import tech.goticket.backendapi.entities.Role;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.repository.AdminRepository;
import tech.goticket.backendapi.repository.RoleRepository;
import tech.goticket.backendapi.repository.UserRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;
import tech.goticket.backendapi.services.AdminService;

import java.time.Instant;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private AdminService adminService;
    private UserStatusRepository userStatusRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminUserConfig(RoleRepository roleRepository,
                           AdminService adminService,
                           UserRepository userRepository,
                           UserStatusRepository userStatusRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.userStatusRepository = userStatusRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        var adminStatus = userStatusRepository.findByName(UserStatus.Values.ACTIVE.name());

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
