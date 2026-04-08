package tech.goticket.backendapi.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.user.Role;
import tech.goticket.backendapi.user.User;
import tech.goticket.backendapi.user.UserStatus;

import java.time.Instant;

@Entity
@Table(name = "tb_admins")
@Getter
@Setter
public class Admin extends User {
    public Admin() { super(); }

    public Admin (
            String email,
            String password,
            Role role,
            UserStatus userStatus,
            String fullName,
            Instant registerDate,
            Instant lastUpdateDate
    ) {
        super(email, password, role, userStatus);
        this.fullName = fullName;
        this.registerDate = registerDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;

    @Override
    public String displayName() {
        return this.getFullName();
    }
}