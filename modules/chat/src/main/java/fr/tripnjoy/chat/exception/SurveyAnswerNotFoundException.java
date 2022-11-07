package fr.tripnjoy.chat.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class SurveyAnswerNotFoundException extends EntityNotFoundException {

    public SurveyAnswerNotFoundException()
    {
    }

    public SurveyAnswerNotFoundException(final String message)
    {
        super(message);
    }

    public SurveyAnswerNotFoundException(final long id)
    {
        this("Answer for survey not found for id " + id);
    }

    public SurveyAnswerNotFoundException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public SurveyAnswerNotFoundException(final Throwable cause)
    {
        super(cause);
    }

    public SurveyAnswerNotFoundException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
