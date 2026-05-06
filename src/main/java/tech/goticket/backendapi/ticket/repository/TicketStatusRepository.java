package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.ticket.enums.TicketStatus;

@Repository
public interface TicketStatusRepository extends JpaRepository<TicketStatus, Long> {
    TicketStatus findByName(String name);
}