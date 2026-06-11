package tech.goticket.backendapi.shared.exception;

public class QueueAdmissionRequiredException extends RuntimeException {
    public QueueAdmissionRequiredException(String message) {
        super(message);
    }
}
