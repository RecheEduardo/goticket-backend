package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.ticket.BatchAllotment;

import java.util.Optional;

public interface BatchAllotmentRepository extends JpaRepository<BatchAllotment, Long> {

    public interface OccupancyView {
        Long getOccupied();
        Long getTotal();
    }

    @Query("""
    SELECT COALESCE(SUM(ba.soldTickets + ba.reservedTickets), 0) AS occupied,
           COALESCE(SUM(ba.quota), 0) AS total
    FROM BatchAllotment ba
    WHERE ba.batch.eventDateSector.eventSector.event.eventId = :eventId
""")
    OccupancyView occupancy(@Param("eventId") Long eventId);
}