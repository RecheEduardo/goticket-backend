package tech.goticket.backendapi.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.dto.MyOrderListItemDTO;
import tech.goticket.backendapi.order.dto.OrderStatusDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    @Query("""
        SELECT new tech.goticket.backendapi.order.dto.OrderStatusDTO(o.orderId, st.name)
        FROM Order o
        JOIN o.status st
        WHERE o.orderId = :orderId
          AND o.buyer.userId = :buyerId
    """)
    Optional<OrderStatusDTO> findStatusByIdAndBuyer(@Param("orderId") Long orderId,
                                                    @Param("buyerId") UUID buyerId);

    @Query("""
        SELECT o.orderId FROM Order o
        WHERE o.status.name = :statusName
          AND o.expiresAt < :cutoff
        ORDER BY o.expiresAt ASC
    """)
    List<Long> findOrderIdsToExpire(@Param("statusName") String statusName,
                                    @Param("cutoff") Instant cutoff,
                                    Pageable pageable);

    @Query(value = """
        SELECT new tech.goticket.backendapi.order.dto.MyOrderListItemDTO(
            o.orderId,
            st.name,
            e.eventId,
            e.title,
            (SELECT img.s3Key FROM EventImage img
              WHERE img.event = e AND img.ordination = 0),
            ed.startDate,
            v.name,
            v.city,
            (SELECT COUNT(oi.orderItemId) FROM OrderItem oi WHERE oi.order = o),
            o.totalPrice,
            o.currency,
            o.placedAt,
            o.expiresAt,
            o.paidAt,
            o.canceledAt
        )
        FROM Order o
        JOIN o.status st
        JOIN o.event e
        JOIN o.eventDate ed
        JOIN e.venue v
        WHERE o.buyer.userId = :buyerId
        ORDER BY o.placedAt DESC
    """,
                countQuery = """
        SELECT COUNT(o.orderId) FROM Order o WHERE o.buyer.userId = :buyerId
    """)
    Page<MyOrderListItemDTO> findMyOrders(@Param("buyerId") UUID buyerId, Pageable pageable);
}
