package fr.tobby.tripnjoyback.exception.auth;

public class TokenExpiredException extends TokenVerificationException {
    public TokenExpiredException()
    {
    }

    public TokenExpiredException(final String message)
    {
        super(message);
    }

    public TokenExpiredException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public TokenExpiredException(final Throwable cause)
    {
        super(cause);
    }

    public TokenExpiredException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
