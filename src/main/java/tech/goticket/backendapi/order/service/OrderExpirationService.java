package tech.goticket.backendapi.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.order.OrderItem;
import tech.goticket.backendapi.order.enums.OrderStatus;
import tech.goticket.backendapi.order.repository.OrderRepository;
import tech.goticket.backendapi.order.repository.OrderStatusRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static tech.goticket.backendapi.order.util.OrderUtils.countByAllotment;

@Service
@RequiredArgsConstructor
public class OrderExpirationService {
    private static final Logger log = LoggerFactory.getLogger(OrderExpirationService.class);

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ReservationService reservationService;

    @Transactional
    public boolean expireIfStillEligible(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("Order {} sumiu entre query e expire — ignorando", orderId);
            return false;
        }

        if (!order.getStatus().isPendingPayment()) {
            log.debug("Order {} já está em {}, não expira", orderId, order.getStatus().getName());
            return false;
        }
        if (order.getExpiresAt().isAfter(Instant.now())) {
            log.debug("Order {} ainda não expirou (race com extensão de TTL?)", orderId);
            return false;
        }

        OrderStatus expiredStatus = orderStatusRepository
                .findByName(OrderStatus.Values.EXPIRED.name())
                .orElseThrow(() -> new IllegalStateException("OrderStatus EXPIRED não existente."));

        order.markExpired(expiredStatus);

        Map<Long, Integer> reservedByAllotment = countByAllotment(order);
        for (Map.Entry<Long, Integer> e : reservedByAllotment.entrySet()) {
            reservationService.releaseReservation(e.getKey(), e.getValue());
        }

        orderRepository.save(order);

        log.info("Order {} expirada. {} ingresso(s) liberado(s) no inventário.",
                orderId, order.getItems().size());

        return true;
    }
}
