package tech.goticket.backendapi.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.order.enums.OrderStatus;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    Optional<OrderStatus> findByName(String name);
}