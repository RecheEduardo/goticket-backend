package tech.goticket.backendapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.goticket.backendapi.controller.dto.LoginRequest;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.controller.dto.UserDTO;
import tech.goticket.backendapi.controller.dto.UserListDTO;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.entities.UserStatus;
import tech.goticket.backendapi.exceptions.user.InactiveUserException;
import tech.goticket.backendapi.services.UserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var user = userService.findByEmail(loginRequest.email());

        if(user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("E-mail ou Senha inválidos!");
        }

        if (user.get().getStatus().getName().equals(UserStatus.Values.INACTIVE.name())) {
            throw new InactiveUserException("Acesso negado, por favor entrar em contato com o suporte da plataforma.");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scope = user.get().getRole().getName();

        var claims = JwtClaimsSet.builder()
                .issuer("goticketbackend")
                .subject(user.get().getUserID().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scope)
                .build();

        var jwtvalue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtvalue, expiresIn));
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getLoggedUser(Authentication authentication){

        UUID userID = UUID.fromString(authentication.getName());

        User user = userService.findById(userID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(new UserDTO(
                user.getUserID(),
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
