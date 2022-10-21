package fr.tripnjoy.users.exception;

public class UserCreationException extends RuntimeException {

    public UserCreationException()
    {
    }

    public UserCreationException(final String message)
    {
        super(message);
    }

    public UserCreationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public UserCreationException(final Throwable cause)
    {
        super(cause);
    }

    public UserCreationException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
