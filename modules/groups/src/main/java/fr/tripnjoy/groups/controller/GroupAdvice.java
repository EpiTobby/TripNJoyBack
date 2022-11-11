package fr.tripnjoy.groups.controller;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.exception.*;
import fr.tripnjoy.users.api.exception.UserNotConfirmedException;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GroupAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GroupAdvice.class);

    private void logError(Exception e)
    {
        logger.debug("Error on request", e);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UserNotConfirmedException.class,
            UpdateGroupException.class,
            JoinGroupFailedException.class,
            GroupCreationException.class,
            UserAlreadyInGroupException.class,
            GroupNotFoundException.class,
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(Exception exception)
    {
        logError(exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception)
    {
        logError(exception);
        return exception.getMessage() != null ? exception.getMessage() : "You are not authorized to perform this operation";
    }
}
