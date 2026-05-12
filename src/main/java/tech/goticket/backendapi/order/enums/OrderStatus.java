package tech.goticket.backendapi.order.enums;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_order_status")
@Getter
@Setter
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @Column(nullable = false, unique = true)
    private String name;

    public boolean isPendingPayment() { return Values.PENDING_PAYMENT.name().equals(this.name); }
    public boolean isPaid()           { return Values.PAID.name().equals(this.name); }
    public boolean isCanceled()       { return Values.CANCELED.name().equals(this.name); }
    public boolean isExpired()        { return Values.EXPIRED.name().equals(this.name); }
    public boolean isRefunded()       { return Values.REFUNDED.name().equals(this.name); }

    public enum Values {
        PENDING_PAYMENT(1L),
        PAID(2L),
        CANCELED(3L),
        EXPIRED(4L),
        REFUNDED(5L);

        private final long statusId;

        Values(long statusId) { this.statusId = statusId; }

        public long getStatusId() { return statusId; }
    }
}
