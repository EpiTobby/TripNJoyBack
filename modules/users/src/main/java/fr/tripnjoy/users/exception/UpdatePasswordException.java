package fr.tripnjoy.users.exception;

public class UpdatePasswordException extends RuntimeException{
    public UpdatePasswordException() {
    }

    public UpdatePasswordException(String message) {
        super(message);
    }

    public UpdatePasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdatePasswordException(Throwable cause) {
        super(cause);
    }

    public UpdatePasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
