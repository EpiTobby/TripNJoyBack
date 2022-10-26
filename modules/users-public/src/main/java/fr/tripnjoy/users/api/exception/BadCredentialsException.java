package fr.tripnjoy.users.api.exception;

import fr.tripnjoy.common.exception.UnauthorizedException;

public class BadCredentialsException extends UnauthorizedException {
    public BadCredentialsException()
    {
    }

    public BadCredentialsException(final String message)
    {
        super(message);
    }

    public BadCredentialsException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public BadCredentialsException(final Throwable cause)
    {
        super(cause);
    }

    public BadCredentialsException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
