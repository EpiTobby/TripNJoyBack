package fr.tripnjoy.users.exception;

public class UserNotConfirmedException extends RuntimeException{
    public UserNotConfirmedException() {
    }

    public UserNotConfirmedException(String message) {
        super(message);
    }

    public UserNotConfirmedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotConfirmedException(Throwable cause) {
        super(cause);
    }

    public UserNotConfirmedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
