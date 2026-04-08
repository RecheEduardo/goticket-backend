package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.EventStatus;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Long> {
    EventStatus findByName(String name);
}
