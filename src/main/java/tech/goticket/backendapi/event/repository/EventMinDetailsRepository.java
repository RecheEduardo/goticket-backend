package tech.goticket.backendapi.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.goticket.backendapi.event.view.EventMinDetailsView;

import java.util.List;

@Repository
public interface EventMinDetailsRepository extends JpaRepository<EventMinDetailsView, Long> {
    Page<EventMinDetailsView> findAllByCategoryId(Long categoryId, Pageable pageable);
}
