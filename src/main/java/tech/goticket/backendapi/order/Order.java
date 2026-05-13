package tech.goticket.backendapi.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import tech.goticket.backendapi.client.Client;
import tech.goticket.backendapi.event.Event;
import tech.goticket.backendapi.event.EventDate;
import tech.goticket.backendapi.order.enums.OrderStatus;
import tech.goticket.backendapi.shared.exception.ForbiddenActionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_orders", indexes = {
        @Index(name = "ix_orders_payment_intent", columnList = "payment_intent_id"),
        @Index(name = "ix_orders_buyer", columnList = "buyer_id"),
        @Index(name = "ix_orders_expires_at", columnList = "expires_at")
})
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long orderId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 80)
    private String idempotencyKey;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "fees_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal feesTotal;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 3)
    private String currency = "BRL";

    @Column(name = "payment_provider", nullable = false, length = 20)
    private String paymentProvider = "STRIPE";

    @Column(name = "payment_intent_id", unique = true, length = 80)
    private String paymentIntentId;

    @Column(name = "payment_method_snapshot", length = 80)
    private String paymentMethodSnapshot;

    @Column(name = "placed_at", nullable = false)
    private Instant placedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Version
    @JsonIgnore
    private Long version = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_date_id", nullable = false)
    private EventDate eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Client buyer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 30)
    private List<OrderItem> items = new ArrayList<>();

    public void markPaid(OrderStatus paidStatus, Instant paidAt) {
        if (!this.status.isPendingPayment()) {
            throw new ForbiddenActionException("Order não está pendente — status atual: " + status.getName());
        }
        this.status = paidStatus;
        this.paidAt = paidAt;
    }

    public void markCanceled(OrderStatus canceledStatus, String reason) {
        if (!this.status.isPendingPayment()) {
            throw new ForbiddenActionException("Só é possível cancelar Order pendente — status atual: " + status.getName());
        }
        this.status = canceledStatus;
        this.canceledAt = Instant.now();
        this.cancelReason = reason;
    }

    public void markExpired(OrderStatus expiredStatus) {
        if (!this.status.isPendingPayment()) return;
        this.status = expiredStatus;
        this.canceledAt = Instant.now();
        this.cancelReason = "Reserva expirada (TTL).";
    }

    public void markRefunded(OrderStatus refundedStatus, Instant refundedAt) {
        if (!this.status.isPaid()) {
            throw new ForbiddenActionException("Só pedidos pagos podem ser reembolsados.");
        }
        this.status = refundedStatus;
        this.refundedAt = refundedAt;
    }

    public boolean canCancel() { return status.isPendingPayment(); }
    public boolean canRefund() { return status.isPaid(); }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}
