package fr.tobby.tripnjoyback.exception;

public class GeocodeAddressException extends RuntimeException{
    public GeocodeAddressException() {
    }

    public GeocodeAddressException(String message) {
        super(message);
    }

    public GeocodeAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeocodeAddressException(Throwable cause) {
        super(cause);
    }

    public GeocodeAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
