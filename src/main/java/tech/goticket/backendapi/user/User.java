package tech.goticket.backendapi.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.goticket.backendapi.shared.exception.user.InactiveUserException;
import tech.goticket.backendapi.shared.model.status.Status;
import tech.goticket.backendapi.user.dto.LoginRequest;

import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class User {

    public User () {}

    public User (
            String email,
            String password,
            Role userRole,
            Status userStatus
    ) {
        this.email = email;
        this.password = password;
        this.role = userRole;
        this.status = userStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userID;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(loginRequest.password(), this.password);
    }

    public abstract String displayName();

    public void validateUserStatus() {
        if (this.getStatus().getName().equals(Status.Values.INACTIVE.name())) {
            throw new InactiveUserException("Acesso negado, por favor entrar em contato com o suporte da plataforma.");
        }
    }
}
