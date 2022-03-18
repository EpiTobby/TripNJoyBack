package fr.tobby.tripnjoyback.exception;

public class ExpiredCodeException extends RuntimeException{
    public ExpiredCodeException() {
        super();
    }

    public ExpiredCodeException(String message) {
        super(message);
    }

    public ExpiredCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredCodeException(Throwable cause) {
        super(cause);
    }

    protected ExpiredCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
