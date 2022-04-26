package fr.tobby.tripnjoyback.exception;

public class MessageNotFoundException extends EntityNotFoundException {

    public MessageNotFoundException()
    {
    }

    public MessageNotFoundException(final String message)
    {
        super(message);
    }

    public MessageNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public MessageNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public MessageNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
