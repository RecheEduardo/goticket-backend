package tech.goticket.backendapi.fee;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.fee.enums.FeeAppliesTo;
import tech.goticket.backendapi.fee.enums.FeeScope;
import tech.goticket.backendapi.fee.enums.FeeType;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_fees", indexes = {
        @Index(name = "ix_fees_scope", columnList = "scope, scope_ref_id, is_active")
})
@Getter
@Setter
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Long feeId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false, length = 20)
    private FeeType feeType;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeeScope scope;

    @Column(name = "scope_ref_id")
    private Long scopeRefId;     // null para PLATFORM; organizer_id ou event_id

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", nullable = false, length = 20)
    private FeeAppliesTo appliesTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;
}