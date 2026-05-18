package tech.goticket.backendapi.payment.service;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.service.OrderPaymentService;
import tech.goticket.backendapi.payment.PaymentEvent;
import tech.goticket.backendapi.payment.PaymentEventRepository;
import tech.goticket.backendapi.shared.exception.payment.StripeDuplicateEventException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {
    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    private final PaymentEventRepository paymentEventRepository;
    private final OrderPaymentService orderPaymentService;

    public void handle(Event event, String rawBody) {
        if(paymentEventRepository.existsByStripeEventId(event.getId())) {
            log.info("Evento Stripe duplicado ignorado: {}", event.getId());
            return;
        }

        PaymentEvent paymentEvent = persistReceipt(event, rawBody);

        try {
            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    PaymentIntent intent = extractPaymentIntent(event);
                    orderPaymentService.markPaidByIntent(intent);
                }

                case "payment_intent.payment_failed",
                     "payment_intent.canceled" -> {
                    PaymentIntent intent = extractPaymentIntent(event);
                    orderPaymentService.cancelByIntent(intent, event.getType());
                }

                case "charge.refunded" -> {
                    orderPaymentService.markRefundedByIntent(event);
                }
                default -> log.info("Evento Stripe não tratado: {}", event.getType());
            }

            paymentEvent.setProcessedAt(Instant.now());
            paymentEventRepository.save(paymentEvent);
        }
        catch (Exception e) {
            paymentEvent.setErrorMessage(e.getMessage());
            paymentEventRepository.save(paymentEvent);
            throw e;
        }
    }

    private PaymentEvent persistReceipt(Event event, String rawBody) {
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setStripeEventId(event.getId());
        paymentEvent.setType(event.getType());
        paymentEvent.setReceivedAt(Instant.now());
        paymentEvent.setPayloadJson(rawBody);
        paymentEvent.setPaymentIntentId(extractIntentIdSafe(event));

        try {
            return paymentEventRepository.save(paymentEvent);
        }
        catch (DataIntegrityViolationException e) {
            log.info("Race detectado em PaymentEvent — tratando como duplicata: {}", event.getId());
            throw new StripeDuplicateEventException(event.getId());
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        StripeObject obj = deserializer.getObject().orElseGet(() -> {
            log.warn("Versão da API do evento {} difere do SDK. Tentando deserializeUnsafe.",
                    event.getId());
            try {
                return deserializer.deserializeUnsafe();
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Falha ao desserializar evento " + event.getId() +
                                " mesmo com deserializeUnsafe", e);
            }
        });

        if (!(obj instanceof PaymentIntent)) {
            throw new IllegalStateException(
                    "Esperava PaymentIntent no evento " + event.getId() +
                            ", recebido: " + obj.getClass().getSimpleName());
        }

        return (PaymentIntent) obj;
    }

    private String extractIntentIdSafe(Event event) {
        try {
            return event.getDataObjectDeserializer().getObject()
                    .map(o -> {
                        if (o instanceof PaymentIntent pi) return pi.getId();
                        return null;
                    })
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
