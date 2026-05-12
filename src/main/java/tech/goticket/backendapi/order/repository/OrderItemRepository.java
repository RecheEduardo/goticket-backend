package tech.goticket.backendapi.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.goticket.backendapi.order.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_OrderId(Long orderId);
}
