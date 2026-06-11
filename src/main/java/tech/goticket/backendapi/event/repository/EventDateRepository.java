package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.event.EventDate;

import java.util.Optional;

public interface EventDateRepository extends JpaRepository<EventDate, Long> {
    @Query("SELECT ed.event.eventId FROM EventDate ed WHERE ed.eventDateId = :eventDateId")
    Optional<Long> findEventIdByEventDateId(@Param("eventDateId") Long eventDateId);
}