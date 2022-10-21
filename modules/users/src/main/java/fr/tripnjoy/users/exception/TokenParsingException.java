package fr.tripnjoy.users.exception;

public class TokenParsingException extends TokenVerificationException {

    public TokenParsingException()
    {
    }

    public TokenParsingException(final String message)
    {
        super(message);
    }

    public TokenParsingException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public TokenParsingException(final Throwable cause)
    {
        super(cause);
    }

    public TokenParsingException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
