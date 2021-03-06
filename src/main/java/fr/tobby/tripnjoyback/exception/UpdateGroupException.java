package fr.tobby.tripnjoyback.exception;

public class UpdateGroupException extends RuntimeException{
    public UpdateGroupException() {
    }

    public UpdateGroupException(String message) {
        super(message);
    }

    public UpdateGroupException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateGroupException(Throwable cause) {
        super(cause);
    }

    public UpdateGroupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
