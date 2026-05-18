package tech.goticket.backendapi.shared.exception.payment;

public class StripeDuplicateEventException extends RuntimeException {
    public StripeDuplicateEventException(String eventId) {
        super("Evento duplicado " + eventId);
    }
}
