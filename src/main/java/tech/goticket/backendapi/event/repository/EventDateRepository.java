package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.EventDate;

public interface EventDateRepository extends JpaRepository<EventDate, Long> {
}