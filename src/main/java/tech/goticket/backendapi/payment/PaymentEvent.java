package tech.goticket.backendapi.payment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_payment_events", indexes = {
        @Index(name = "ix_payment_events_intent", columnList = "payment_intent_id")
})
@Getter
@Setter
public class PaymentEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_event_id")
    private Long paymentEventId;

    @Column(name = "stripe_event_id", nullable = false, unique = true, length = 80)
    private String stripeEventId;

    @Column(nullable = false, length = 80)
    private String type;

    @Column(name = "payment_intent_id", length = 80)
    private String paymentIntentId;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Lob
    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "error_message", length = 500)
    private String errorMessage;
}
