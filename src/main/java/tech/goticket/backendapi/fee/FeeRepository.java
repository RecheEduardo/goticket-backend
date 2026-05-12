package tech.goticket.backendapi.fee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.fee.enums.FeeScope;

import java.time.Instant;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("""
        SELECT f FROM Fee f
        WHERE f.isActive = true
          AND f.scope = :scope
          AND (:scopeRefId IS NULL AND f.scopeRefId IS NULL OR f.scopeRefId = :scopeRefId)
          AND f.effectiveFrom <= :at
          AND (f.effectiveTo IS NULL OR f.effectiveTo > :at)
    """)
    List<Fee> findActiveByScope(@Param("scope") FeeScope scope,
                                @Param("scopeRefId") Long scopeRefId,
                                @Param("at") Instant at);
}