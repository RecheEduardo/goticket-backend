package tech.goticket.backendapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.goticket.backendapi.controller.dto.LoginRequest;
import tech.goticket.backendapi.controller.dto.LoginResponse;
import tech.goticket.backendapi.entities.User;
import tech.goticket.backendapi.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder,
                           UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var user = userRepository.findByEmail(loginRequest.email());

        if(user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("E-mail ou Senha inválidos!");
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

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listAllUsers(){
        var users = userRepository.findAll();

        return ResponseEntity.ok(users);
    }
}
