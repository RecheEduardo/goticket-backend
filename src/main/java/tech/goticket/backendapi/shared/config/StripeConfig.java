package tech.goticket.backendapi.shared.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StripeConfig {
    private static final Logger log = LoggerFactory.getLogger(StripeConfig.class);

    @Value("${stripe.api.key}")
    private String apiKey;

    @Value("${stripe.api.timeout-ms:30000}")
    private int timeoutMs;

    @Value("${stripe.api.max-network-retries:2}")
    private int maxNetworkRetries;

    @PostConstruct
    public void initStripe() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "STRIPE_SECRET_KEY não configurada — defina no .env.local"
            );
        }

        Stripe.apiKey = apiKey;
        Stripe.setConnectTimeout(timeoutMs);
        Stripe.setReadTimeout(timeoutMs);
        Stripe.setMaxNetworkRetries(maxNetworkRetries);

        log.info("Stripe SDK inicializado (mode={}, timeoutMs={}, retries={})",
                apiKey.startsWith("sk_test_") ? "TEST" : "LIVE",
                timeoutMs,
                maxNetworkRetries);
    }
}
