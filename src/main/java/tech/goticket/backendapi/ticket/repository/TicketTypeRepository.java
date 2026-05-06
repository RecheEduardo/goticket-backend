package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.ticket.TicketType;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    TicketType findByName(String name);
}