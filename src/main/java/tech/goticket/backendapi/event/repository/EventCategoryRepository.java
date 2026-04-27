package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
