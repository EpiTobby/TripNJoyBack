package fr.tripnjoy.users.controller;

import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.users.api.exception.BadCredentialsException;
import fr.tripnjoy.users.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({
            UnauthorizedException.class,
            BadCredentialsException.class,
            BadConfirmationCodeException.class,
            ExpiredCodeException.class,
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String getError(UnauthorizedException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler({
            UserCreationException.class,
            UpdateEmailException.class,
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler({
            UpdatePasswordException.class,
            UserAlreadyConfirmedException.class,
            ForbiddenOperationException.class,
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(UpdatePasswordException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFoundException(EntityNotFoundException exception)
    {
        return exception.getMessage();
    }
}
