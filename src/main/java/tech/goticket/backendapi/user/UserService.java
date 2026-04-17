package tech.goticket.backendapi.user;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.user.dto.UserDTO;
import tech.goticket.backendapi.user.dto.UserListDTO;
import tech.goticket.backendapi.user.repository.UserRepository;
import tech.goticket.backendapi.shared.model.status.StatusRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Transactional
    public UserListDTO findAll(PageRequest request) {
        var users = userRepository.findAll(request).map(user -> new UserDTO(user.getUserID(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()));

        return new UserListDTO(request.getPageNumber(),
                request.getPageSize(),
                users.getTotalPages(),
                users.getTotalElements(),
                users.toList());
    }

    @Transactional
    public UserListDTO findActiveUsers(PageRequest request) {
        var activeStatus = statusRepository.findByName(Status.Values.ACTIVE.name());
        var users = userRepository.findByStatus(activeStatus, request)
                .map(user -> new UserDTO(user.getUserID(),
                        user.getEmail(),
                        user.getRole(),
                        user.getStatus()));

        return new UserListDTO(request.getPageNumber(),
                request.getPageSize(),
                users.getTotalPages(),
                users.getTotalElements(),
                users.toList());
    }

    public Optional<User> findById(UUID userID) { return userRepository.findById(userID); }

    public Optional<User> findByEmail(String email) { return userRepository.findByEmail(email); }

}
