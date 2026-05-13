package tech.goticket.backendapi.shared.exception;

public class ReservationContentionException extends RuntimeException {
    public ReservationContentionException(String message) {
        super(message);
    }
}
