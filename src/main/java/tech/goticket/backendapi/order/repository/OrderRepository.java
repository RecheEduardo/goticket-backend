package tech.goticket.backendapi.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.order.Order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    Page<Order> findByBuyer_UserId(UUID buyerId, Pageable pageable);

    List<Order> findByStatus_NameAndExpiresAtBefore(String statusName, Instant cutoff);
}
