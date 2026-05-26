package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.EventDateSector;

public interface EventDateSectorRepository extends JpaRepository<EventDateSector, Long> {
}
