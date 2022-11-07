package fr.tripnjoy.chat.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class SurveyNotFoundException extends EntityNotFoundException {

    public SurveyNotFoundException()
    {
    }

    public SurveyNotFoundException(final String message)
    {
        super(message);
    }

    public SurveyNotFoundException(final long id)
    {
        this("Survey not found for id " + id);
    }

    public SurveyNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public SurveyNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public SurveyNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
