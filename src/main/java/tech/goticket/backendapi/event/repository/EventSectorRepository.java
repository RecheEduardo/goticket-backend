package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.EventSector;

public interface EventSectorRepository extends JpaRepository<EventSector, Long> {
    long countByVenueSector_SectorId(Long venueSectorId);
}
