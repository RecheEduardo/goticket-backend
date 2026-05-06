package tech.goticket.backendapi.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.user.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Optional<Admin> findById(UUID adminId) { return adminRepository.findByUserId(adminId); }

    public Admin saveAdmin(Admin newAdmin) { return adminRepository.save(newAdmin); }
}
