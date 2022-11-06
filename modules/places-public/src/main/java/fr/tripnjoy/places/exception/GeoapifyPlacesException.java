package fr.tripnjoy.places.exception;

public class GeoapifyPlacesException extends RuntimeException{
    public GeoapifyPlacesException() {
    }

    public GeoapifyPlacesException(String message) {
        super(message);
    }

    public GeoapifyPlacesException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeoapifyPlacesException(Throwable cause) {
        super(cause);
    }

    public GeoapifyPlacesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
