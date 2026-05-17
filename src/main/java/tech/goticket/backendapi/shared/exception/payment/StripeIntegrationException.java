package tech.goticket.backendapi.shared.exception.payment;

public class StripeIntegrationException extends RuntimeException {
    public StripeIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
    public StripeIntegrationException(String message) {
        super(message);
    }
}
