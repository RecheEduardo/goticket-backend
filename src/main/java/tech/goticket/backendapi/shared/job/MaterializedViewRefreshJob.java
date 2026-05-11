package tech.goticket.backendapi.shared.job;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaterializedViewRefreshJob {

    private static final Logger log = LoggerFactory.getLogger(MaterializedViewRefreshJob.class);
    private static final String VIEW_NAME = "vw_event_min_details";

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void refreshEventSummaryView() {
        if (isPopulated()) {
            entityManager.createNativeQuery(
                    "REFRESH MATERIALIZED VIEW CONCURRENTLY " + VIEW_NAME
            ).executeUpdate();
        } else {
            log.warn("Materialized view {} not yet populated. Running first REFRESH without CONCURRENTLY.", VIEW_NAME);
            entityManager.createNativeQuery(
                    "REFRESH MATERIALIZED VIEW " + VIEW_NAME
            ).executeUpdate();
        }
    }

    private boolean isPopulated() {
        Boolean populated = (Boolean) entityManager.createNativeQuery(
                "SELECT relispopulated FROM pg_class WHERE relname = :name"
        ).setParameter("name", VIEW_NAME).getSingleResult();
        return Boolean.TRUE.equals(populated);
    }
}
