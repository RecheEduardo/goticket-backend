package tech.goticket.backendapi.user.token;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenFamilyRevoker {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void revokeFamily(String familyId) {
        refreshTokenRepository.revokeFamily(familyId);
    }
}
