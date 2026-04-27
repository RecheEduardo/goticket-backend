package tech.goticket.backendapi.user.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.goticket.backendapi.user.User;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token", columnList = "token", unique = true),
        @Index(name = "idx_refresh_family", columnList = "familyId")
})
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt;

    private boolean revoked = false;

    public RefreshToken(User user, String familyId, long ttlSeconds) {
        this.token = UUID.randomUUID().toString();
        this.familyId = familyId;
        this.user = user;
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plusSeconds(ttlSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isUsable() {
        return !this.revoked && !this.isExpired();
    }
}
