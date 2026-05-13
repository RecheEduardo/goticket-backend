package tech.goticket.backendapi.ticket.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.ticket.BatchAllotment;

import java.util.Optional;

public interface BatchAllotmentRepository extends JpaRepository<BatchAllotment, Long> {
}