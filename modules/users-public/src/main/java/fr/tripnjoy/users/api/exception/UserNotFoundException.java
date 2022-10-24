package fr.tripnjoy.users.api.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException()
    {
    }

    public UserNotFoundException(long userId)
    {
        this("No user found with id " + userId);
    }

    public UserNotFoundException(final String message)
    {
        super(message);
    }

    public UserNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public UserNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public UserNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
