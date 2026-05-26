package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.event.EventImage;

import java.util.List;
import java.util.Optional;

public interface EventImageRepository extends JpaRepository<EventImage, Long> {
    List<EventImage> findByEvent_EventId(Long eventId);

    @Query("SELECT img.s3Key FROM EventImage img WHERE img.event.eventId = :eventId AND img.ordination = 0")
    Optional<String> findMainImageKey(@Param("eventId") Long eventId);
}
