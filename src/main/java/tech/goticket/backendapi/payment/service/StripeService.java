package tech.goticket.backendapi.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.shared.exception.payment.StripeIntegrationException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripeService {
    private static final Logger log = LoggerFactory.getLogger(StripeService.class);

    public PaymentIntent createPaymentIntent(Order order, String idempotencyKey) {
        long amountInCents = order.getTotalPrice()
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(order.getCurrency().toLowerCase())
                .putMetadata("order_id", order.getOrderId().toString())
                .putMetadata("buyer_id", order.getBuyer().getUserId().toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .setDescription("GoTicket order #" + order.getOrderId())
                .build();

        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey("pi_create_" + idempotencyKey)
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params, options);
            log.info("PaymentIntent criado: orderId={}, intentId={}, amount= {} {}",
                    order.getOrderId(), intent.getId(), amountInCents, order.getCurrency());
            return intent;
        } catch (StripeException e) {
            log.error("Falha ao criar PaymentIntent para orderId={}: {}",
                    order.getOrderId(), e.getMessage(), e);
            throw new StripeIntegrationException(
                    "Não foi possível criar PaymentIntent para Order " + order.getOrderId(), e);
        }
    }

    public void tryCancelPaymentIntent(String paymentIntentId, String reason) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

            String status = intent.getStatus();
            if ("succeeded".equals(status) || "canceled".equals(status)) {
                log.info("PI {} já está {} — sem ação", paymentIntentId, status);
                return;
            }

            PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
                    .setCancellationReason(PaymentIntentCancelParams.CancellationReason.REQUESTED_BY_CUSTOMER)
                    .build();
            intent.cancel(params);
            log.info("PI {} cancelado na Stripe ({}).", paymentIntentId, reason);

        } catch (StripeException e) {
            log.warn("Falha (não-fatal) ao cancelar PI {}: {}", paymentIntentId, e.getMessage());
        }
    }

    public String retrieveClientSecret(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId).getClientSecret();
        } catch (StripeException e) {
            throw new StripeIntegrationException(
                    "Falha ao recuperar PaymentIntent existente: " + paymentIntentId, e);
        }
    }
}
