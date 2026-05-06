package tech.goticket.backendapi.venue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueSectorRepository extends JpaRepository<VenueSector, Long> {
    List<VenueSector> findAllByVenue_VenueIdOrderBySectorIdAsc(Long venueId);
    Optional<VenueSector> findBySectorIdAndVenue_VenueId(Long sectorId, Long venueId);
}
