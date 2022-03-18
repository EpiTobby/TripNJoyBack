package fr.tobby.tripnjoyback.exception;

public class UpdateEmailException extends RuntimeException{
    public UpdateEmailException() {
    }

    public UpdateEmailException(String message) {
        super(message);
    }

    public UpdateEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateEmailException(Throwable cause) {
        super(cause);
    }

    public UpdateEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
