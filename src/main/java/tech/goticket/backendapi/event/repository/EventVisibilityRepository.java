package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.enums.EventVisibility;

import java.util.Optional;

public interface EventVisibilityRepository extends JpaRepository<EventVisibility, Long> {
    Optional<EventVisibility> findByName(String name);
}
