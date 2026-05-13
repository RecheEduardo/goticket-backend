package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.ticket.BatchAllotment;

public interface BatchAllotmentRepository extends JpaRepository<BatchAllotment, Long> {
}