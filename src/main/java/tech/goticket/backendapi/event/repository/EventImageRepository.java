package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.event.EventImage;

import java.util.List;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    List<EventImage> findByEvent_EventID(Long eventId);
}
