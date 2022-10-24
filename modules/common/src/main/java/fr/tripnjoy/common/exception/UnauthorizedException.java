package fr.tripnjoy.common.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException()
    {
    }

    public UnauthorizedException(final String message)
    {
        super(message);
    }

    public UnauthorizedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public UnauthorizedException(final Throwable cause)
    {
        super(cause);
    }

    public UnauthorizedException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
