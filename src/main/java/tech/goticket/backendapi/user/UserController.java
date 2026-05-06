package tech.goticket.backendapi.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.user.dto.*;
import tech.goticket.backendapi.user.token.AuthTokenService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthTokenService authTokenService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var user = userService.findByEmail(loginRequest.email());

        user.ifPresent(User::validateUserStatus);

        if(user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("E-mail ou Senha inválidos!");
        }

        return ResponseEntity.ok(authTokenService.issueTokens(user.get()));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authTokenService.refreshTokens(request.refreshToken()));
    }

    @PostMapping("/auth/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authTokenService.revokeAll(UUID.fromString(authentication.getName()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getLoggedUser(Authentication authentication){

        UUID userID = UUID.fromString(authentication.getName());

        User user = userService.findById(userID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(new UserDTO(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        ));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserListDTO> listActiveUsers(@RequestParam(name = "page",defaultValue = "0") int page,
                                                       @RequestParam(name = "page",defaultValue = "10") int pageSize){
        var users = userService.findActiveUsers(PageRequest.of(page,pageSize, Sort.Direction.ASC, "email"));

        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<UserListDTO> listAllUsers(@RequestParam(name = "page",defaultValue = "0") int page,
                                                    @RequestParam(name = "page",defaultValue = "10") int pageSize){
        var users = userService.findAll(PageRequest.of(page,pageSize, Sort.Direction.ASC, "email"));

        return ResponseEntity.ok(users);
    }
}
