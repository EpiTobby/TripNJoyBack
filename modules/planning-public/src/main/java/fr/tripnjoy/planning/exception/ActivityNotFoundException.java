package fr.tripnjoy.planning.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class ActivityNotFoundException extends EntityNotFoundException {
    public ActivityNotFoundException()
    {
    }

    public ActivityNotFoundException(final String message)
    {
        super(message);
    }

    public ActivityNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ActivityNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public ActivityNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
