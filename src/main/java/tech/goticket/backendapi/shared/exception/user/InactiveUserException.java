package tech.goticket.backendapi.shared.exception.user;

public class InactiveUserException extends RuntimeException {
    public InactiveUserException(String message) {
        super(message);
    }
}
