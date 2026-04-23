package tech.goticket.backendapi.shared.job;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefreshJob {
    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void refreshEventSummaryView() {
        entityManager.createNativeQuery(
                "REFRESH MATERIALIZED VIEW vw_event_min_details"
        ).executeUpdate();
    }
}
