package fr.tripnjoy.groups.exception;

public class UserAlreadyInGroupException extends RuntimeException {
    public UserAlreadyInGroupException() {
    }

    public UserAlreadyInGroupException(String message) {
        super(message);
    }

    public UserAlreadyInGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyInGroupException(Throwable cause) {
        super(cause);
    }

    public UserAlreadyInGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
