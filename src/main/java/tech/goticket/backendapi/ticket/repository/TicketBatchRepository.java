package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.ticket.TicketBatch;

public interface TicketBatchRepository extends JpaRepository<TicketBatch, Long> {

    @Query("SELECT COALESCE(MAX(b.batchNumber), 0) FROM TicketBatch b WHERE b.eventDateSector.eventDateSectorId = :eventDateSectorId")
    int findMaxBatchNumber(@Param("eventDateSectorId") Long eventDateSectorId);
}
