package fr.tripnjoy.common.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException()
    {
    }

    public EntityNotFoundException(final String message)
    {
        super(message);
    }

    public EntityNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public EntityNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public EntityNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
