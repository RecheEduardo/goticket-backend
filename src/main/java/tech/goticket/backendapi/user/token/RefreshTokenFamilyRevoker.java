package tech.goticket.backendapi.user.token;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenFamilyRevoker {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void revokeFamily(String familyId) {
        refreshTokenRepository.revokeFamily(familyId);
    }
}
