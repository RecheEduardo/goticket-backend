package tech.goticket.backendapi.demand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_event_demand_profiles")
@Getter
@Setter
public class EventDemandProfile {
    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Column(nullable = false, length = 20)
    private String tier = "NORMAL";

    @Column(nullable = false, length = 20)
    private String source = "AUTO";

    @Column(name = "sales_velocity_per_min", precision = 8, scale = 2)
    private BigDecimal salesVelocityPerMin;

    @Column(name = "occupancy_rate", precision = 5, scale = 4)
    private BigDecimal occupancyRate;

    @Column(name = "last_evaluated_at", nullable = false)
    private Instant lastEvaluatedAt;

    @Column(name = "manual_override_until")
    private Instant manualOverrideUntil;

    public boolean isHigh()   { return "HIGH".equals(tier); }
    public boolean isManual() { return "MANUAL".equals(source); }
}
