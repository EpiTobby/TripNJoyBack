package fr.tripnjoy.users.exception;

public class TokenVerificationException extends Exception {

    public TokenVerificationException()
    {
    }

    public TokenVerificationException(final String message)
    {
        super(message);
    }

    public TokenVerificationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public TokenVerificationException(final Throwable cause)
    {
        super(cause);
    }

    public TokenVerificationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
