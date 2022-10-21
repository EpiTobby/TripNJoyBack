package fr.tripnjoy.users.exception;

public class BadConfirmationCodeException extends RuntimeException{
    public BadConfirmationCodeException() {
        super();
    }

    public BadConfirmationCodeException(String message) {
        super(message);
    }

    public BadConfirmationCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConfirmationCodeException(Throwable cause) {
        super(cause);
    }

    protected BadConfirmationCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
