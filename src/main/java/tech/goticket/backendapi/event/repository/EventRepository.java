package tech.goticket.backendapi.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByEventId(Long eventId);

    Page<Event> findByOrganizer_UserId(UUID organizerId, Pageable pageable);

    @Override
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "venue",
                    "category"
            }
    )
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    @Query("""
        SELECT e.eventId FROM Event e
        WHERE e.status.name = 'APPROVED' AND e.endDate > :now
    """)
    List<Long> findActiveEventIdsForDemand(@Param("now") LocalDateTime now);
}
