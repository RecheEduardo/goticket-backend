package tech.goticket.backendapi.demand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.demand.EventDemandProfile;

import java.util.Optional;

public interface EventDemandProfileRepository extends JpaRepository<EventDemandProfile, Long> {
    Optional<EventDemandProfile> findByEventId(Long eventId);
}
