package tech.goticket.backendapi.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.goticket.backendapi.sector.Sector;
import tech.goticket.backendapi.venue.VenueSector;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_event_sectors")
@Getter
@Setter
public class EventSector extends Sector {

    @Column(name = "has_numbered_seats", nullable = false)
    private boolean hasNumberedSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "venue_sector_id", nullable = false)
    private VenueSector venueSector;

    @OneToMany(mappedBy = "eventSector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventDateSector> dateSectors = new ArrayList<>();

    @JsonProperty("venueSectorId")
    public Long getVenueSectorId() {
        return venueSector != null ? venueSector.getSectorId() : null;
    }

    @JsonProperty("mapElementId")
    public String getMapElementId() {
        return venueSector != null ? venueSector.getMapElementId() : null;
    }
}
