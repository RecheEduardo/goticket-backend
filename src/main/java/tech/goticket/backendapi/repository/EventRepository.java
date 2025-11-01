package tech.goticket.backendapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.controller.projections.EventMinProjection;
import tech.goticket.backendapi.entities.Event;
import tech.goticket.backendapi.entities.EventStatus;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventID(Long eventID);
    Page<EventMinProjection> findAllEventsByStatus(EventStatus eventStatus, Pageable pageable);
}
