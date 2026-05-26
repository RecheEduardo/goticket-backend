package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.ticket.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.allotment a
        JOIN FETCH a.ticketType
        JOIN FETCH a.batch b
        JOIN FETCH b.eventDateSector eds
        JOIN FETCH eds.eventSector
        JOIN FETCH eds.eventDate ed
        JOIN FETCH ed.event e
        JOIN FETCH e.venue
        JOIN FETCH t.status
        LEFT JOIN FETCH t.eligibilityType
        WHERE t.ticketId IN (
            SELECT oi.ticket.ticketId FROM OrderItem oi
            WHERE oi.order.orderId = :orderId AND oi.ticket IS NOT NULL
        )
        ORDER BY t.registerDate ASC
    """)
    List<Ticket> findByOrderIdWithGraph(@Param("orderId") Long orderId);

    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.allotment a
        JOIN FETCH a.ticketType
        JOIN FETCH a.batch b
        JOIN FETCH b.eventDateSector eds
        JOIN FETCH eds.eventSector
        JOIN FETCH eds.eventDate ed
        JOIN FETCH ed.event e
        JOIN FETCH e.venue
        JOIN FETCH t.status
        LEFT JOIN FETCH t.eligibilityType
        WHERE t.buyer.userId = :buyerId
        ORDER BY t.registerDate DESC
    """)
    List<Ticket> findByBuyerWithGraph(@Param("buyerId") UUID buyerId);

    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.allotment a
        JOIN FETCH a.ticketType
        JOIN FETCH a.batch b
        JOIN FETCH b.eventDateSector eds
        JOIN FETCH eds.eventSector
        JOIN FETCH eds.eventDate ed
        JOIN FETCH ed.event e
        JOIN FETCH e.venue
        JOIN FETCH t.status
        JOIN FETCH t.buyer
        LEFT JOIN FETCH t.eligibilityType
        WHERE t.ticketId = :ticketId
    """)
    Optional<Ticket> findByIdWithGraph(@Param("ticketId") UUID ticketId);
}
