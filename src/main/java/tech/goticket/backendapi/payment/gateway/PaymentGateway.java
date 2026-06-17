package tech.goticket.backendapi.payment.gateway;

import tech.goticket.backendapi.order.Order;

public interface PaymentGateway {
    PaymentIntentResult createIntent(Order order, String idempotencyKey);
    String fetchClientSecret(String paymentIntentId);
    void tryCancel(String paymentIntentId, String reason);

    record PaymentIntentResult(String id, String clientSecret) {}
}