package tech.goticket.backendapi.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.Event;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventId(Long eventId);

    Page<Event> findByOrganizer_UserId(UUID organizerId, Pageable pageable);
}
