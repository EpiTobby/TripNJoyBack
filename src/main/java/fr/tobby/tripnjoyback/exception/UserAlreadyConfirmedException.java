package fr.tobby.tripnjoyback.exception;

public class UserAlreadyConfirmedException extends RuntimeException{
    public UserAlreadyConfirmedException() {
        super();
    }

    public UserAlreadyConfirmedException(String message) {
        super(message);
    }

    public UserAlreadyConfirmedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyConfirmedException(Throwable cause) {
        super(cause);
    }

    protected UserAlreadyConfirmedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
