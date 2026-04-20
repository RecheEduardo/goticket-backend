package tech.goticket.backendapi.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.projection.EventMinProjection;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventStatus;
import tech.goticket.backendapi.event.EventVisibility;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventID(Long eventID);

    @EntityGraph(attributePaths = "venue")
    Page<EventMinProjection> findAllEventsByStatusAndEventVisibility(EventStatus eventStatus, EventVisibility eventVisibility, Pageable pageable);
}
