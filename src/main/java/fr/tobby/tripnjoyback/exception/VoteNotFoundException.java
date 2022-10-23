package fr.tobby.tripnjoyback.exception;

public class VoteNotFoundException extends EntityNotFoundException {

    public VoteNotFoundException()
    {
    }

    public VoteNotFoundException(final String message)
    {
        super(message);
    }

    public VoteNotFoundException(final long id)
    {
        this("Survey not found for id " + id);
    }

    public VoteNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public VoteNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public VoteNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
