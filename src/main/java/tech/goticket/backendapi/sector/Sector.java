package tech.goticket.backendapi.sector;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sector_id", nullable = false)
    private Long sectorID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "register_date", nullable = false)
    private Instant registerDate;

    @Column(name = "last_update_date", nullable = false)
    private Instant lastUpdateDate;
}
