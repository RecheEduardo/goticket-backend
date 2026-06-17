package tech.goticket.backendapi.payment.gateway;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.Order;

@Service
@Profile("loadtest")
public class FakePaymentGateway implements PaymentGateway {
    public PaymentIntentResult createIntent(Order order, String idempotencyKey) {
        return new PaymentIntentResult("pi_load_" + order.getOrderId(),
                                "secret_load_" + order.getOrderId());
    }

    public String fetchClientSecret(String id) { return "secret_load_replay"; }
    public void tryCancel(String id, String reason) {};
}
