package tech.goticket.backendapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.entities.Admin;
import tech.goticket.backendapi.repository.AdminRepository;
import tech.goticket.backendapi.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<Admin> findById(UUID adminID) { return adminRepository.findByUserID(adminID); }

    public Admin saveAdmin(Admin newAdmin) { return adminRepository.save(newAdmin); }
}
