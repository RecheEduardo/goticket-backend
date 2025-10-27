package tech.goticket.backendapi.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.controller.dto.UserListDTO;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.repository.UserRepository;
import tech.goticket.backendapi.repository.UserStatusRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Transactional
    public List<UserListDTO> findAll() {
        var users = userRepository.findAll();
        return users.stream().map(user -> new UserListDTO(user.getUserID(),
                                                            user.getEmail(),
                                                            user.getRole(),
                                                            user.getStatus())).toList();
    }

    @Transactional
    public List<UserListDTO> findActiveUsers() {
        var activeStatus = userStatusRepository.findByName(UserStatus.Values.ACTIVE.name());
        var users = userRepository.findByStatus(activeStatus);

        return users.stream().map(user -> new UserListDTO(user.getUserID(),
                user.getEmail(),
                user.getRole(),
                user.getStatus())).toList();
    }

    public Optional<User> findById(UUID userID) { return userRepository.findById(userID); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

}
