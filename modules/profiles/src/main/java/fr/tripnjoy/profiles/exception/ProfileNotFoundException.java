package fr.tripnjoy.profiles.exception;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException() {
        super();
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ProfileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
