package fr.tobby.tripnjoyback.exception;

public class BadAvailabilityException extends RuntimeException{
    public BadAvailabilityException() {
    }

    public BadAvailabilityException(String message) {
        super(message);
    }

    public BadAvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadAvailabilityException(Throwable cause) {
        super(cause);
    }

    public BadAvailabilityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
