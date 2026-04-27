package tech.goticket.backendapi.user.token;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.shared.exception.user.InvalidRefreshTokenException;
import tech.goticket.backendapi.shared.exception.user.RefreshTokenReuseException;
import tech.goticket.backendapi.user.User;

import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenFamilyRevoker refreshTokenFamilyRevoker;

    @Value("${jwt.refresh.ttl:604800}")
    private long refreshTtl;

    /**
     * Cria um refresh token para um novo login (nova família).
     */
    public RefreshToken createForLogin(User user) {
        String familyId = UUID.randomUUID().toString();
        RefreshToken rt = new RefreshToken(user, familyId, refreshTtl);
        return refreshTokenRepository.save(rt);
    }

    /**
     * Rotaciona o refresh token: valida o antigo, revoga-o e cria um novo
     * na mesma família. Se detectar reuso, revoga toda a família.
     */
    @Transactional
    public RefreshToken rotate(String tokenValue) {
        RefreshToken existing = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token não encontrado."));

        if (existing.isRevoked()) {
            refreshTokenFamilyRevoker.revokeFamily(existing.getFamilyId());
            throw new RefreshTokenReuseException(
                    "Reuso de refresh token detectado! Toda a sessão foi invalidada por segurança."
            );
        }

        if (existing.isExpired()) {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
            throw new InvalidRefreshTokenException("Refresh token expirado.");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        RefreshToken newToken = new RefreshToken(existing.getUser(), existing.getFamilyId(), refreshTtl);
        return refreshTokenRepository.save(newToken);
    }

    /**
     * Revoga todos os tokens de um usuário (logout de todas as sessões).
     */
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllByUser(userId);
    }
}
