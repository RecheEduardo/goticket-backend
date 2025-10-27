package tech.goticket.backendapi.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.controller.dto.UserListDTO;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<UserListDTO> findAll() {
        var users = userRepository.findAll();
        return users.stream().map(x -> new UserListDTO(x.getUserID(),
                                                            x.getEmail(),
                                                            x.getRole(),
                                                            x.getStatus())).toList();
    }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }
}
