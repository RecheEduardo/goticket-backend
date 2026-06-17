package tech.goticket.backendapi.payment.gateway;

import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tech.goticket.backendapi.order.Order;
import tech.goticket.backendapi.payment.service.StripeService;

@Service
@Profile("!loadtest")
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway{
    private final StripeService stripeService;

    public PaymentIntentResult createIntent(Order order, String idempotencyKey) {
        PaymentIntent pi = stripeService.createPaymentIntent(order, idempotencyKey);
        return new PaymentIntentResult(pi.getId(), pi.getClientSecret());
    }

    public String fetchClientSecret(String id) { return stripeService.retrieveClientSecret(id); }
    public void tryCancel(String id, String reason) { stripeService.tryCancelPaymentIntent(id, reason); }
}
