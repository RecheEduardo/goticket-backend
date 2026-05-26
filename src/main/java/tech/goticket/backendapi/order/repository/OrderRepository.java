package tech.goticket.backendapi.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.order.Order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    @Query("""
        SELECT o.orderId FROM Order o
        WHERE o.status.name = :statusName
          AND o.expiresAt < :cutoff
        ORDER BY o.expiresAt ASC
    """)
    List<Long> findOrderIdsToExpire(@Param("statusName") String statusName,
                                    @Param("cutoff") Instant cutoff,
                                    Pageable pageable);

    Page<Order> findByBuyer_UserId(UUID buyerId, Pageable pageable);
}
