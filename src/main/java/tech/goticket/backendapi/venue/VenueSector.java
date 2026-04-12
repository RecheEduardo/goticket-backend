package tech.goticket.backendapi.venue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.sector.Sector;

@Entity
@Table(name = "tb_venue_sectors")
@Getter
@Setter
public class VenueSector extends Sector {
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
}