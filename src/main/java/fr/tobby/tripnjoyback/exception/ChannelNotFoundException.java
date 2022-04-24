package fr.tobby.tripnjoyback.exception;

public class ChannelNotFoundException extends RuntimeException {

    public ChannelNotFoundException()
    {
    }

    public ChannelNotFoundException(final String message)
    {
        super(message);
    }

    public ChannelNotFoundException(final long id)
    {
        this("Channel not found for id " + id);
    }

    public ChannelNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ChannelNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public ChannelNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
