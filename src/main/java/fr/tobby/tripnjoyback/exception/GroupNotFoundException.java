package fr.tobby.tripnjoyback.exception;

public class GroupNotFoundException extends EntityNotFoundException {
    public GroupNotFoundException()
    {
        super();
    }

    public GroupNotFoundException(String message)
    {
        super(message);
    }

    public GroupNotFoundException(long groupId)
    {
        this("No group found with id " + groupId);
    }

    public GroupNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GroupNotFoundException(Throwable cause)
    {
        super(cause);
    }

    protected GroupNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
