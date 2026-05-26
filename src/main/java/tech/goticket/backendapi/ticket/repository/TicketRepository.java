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
        SELECT oi.ticket
        FROM OrderItem oi
        WHERE oi.order.orderId = :orderId
          AND oi.ticket IS NOT NULL
        ORDER BY oi.ticket.registerDate ASC
    """)
    List<Ticket> findByOrderId(@Param("orderId") Long orderId);

    List<Ticket> findByBuyer_UserIdOrderByRegisterDateDesc(UUID buyerId);

    Optional<Ticket> findByQrToken(String qrToken);
}
