package tech.goticket.backendapi.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.view.EventMinDetailsView;

@Repository
public interface EventMinDetailsRepository extends JpaRepository<EventMinDetailsView, Long> {
}
