package fr.tobby.tripnjoyback.exception;

public class ScanException extends RuntimeException {
    public ScanException()
    {
    }

    public ScanException(final String message)
    {
        super(message);
    }

    public ScanException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ScanException(final Throwable cause)
    {
        super(cause);
    }

    public ScanException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
