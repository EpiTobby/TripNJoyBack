package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.*;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordModel;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordModel;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordModel;
import fr.tobby.tripnjoyback.model.response.UserIdModel;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("")
    public Iterable<UserEntity> getAll()
    {
        return userService.getAll();
    }

    @PostMapping("")
    public UserModel create(@RequestBody UserCreationModel model)
    {
        return userService.createUser(model);
    }

    @GetMapping("{id}")
    public UserModel getUserById(@PathVariable("id") final long userId)
    {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    @PatchMapping("{id}/confirm")
    public boolean confirmUser(@PathVariable("id") final long userId, @RequestBody ConfirmationCodeModel confirmationCode)
    {
        return userService.confirmUser(userId,confirmationCode);
    }

    @PostMapping("forgotpassword")
    public boolean forgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel)
    {
        return userService.forgotPassword(forgotPasswordModel);
    }

    @PatchMapping("validatecodepassword")
    public UserIdModel validateCodePassword(@RequestBody ValidateCodePasswordModel validateCodePasswordModel)
    {
        return userService.validateCodePassword(validateCodePasswordModel);
    }

    @PatchMapping("{id}/updatepassword")
    public boolean UpdatePassword(@PathVariable("id") final long userId, @RequestBody UpdatePasswordModel updatePasswordModel)
    {
        return userService.updatePassword(userId, updatePasswordModel);
    }

    @PatchMapping("{id}/phone")
    public UserModel UpdatePhoneNumber(@PathVariable("id") final long userId, String phoneNumber)
    {
        return userService.updatePhoneNumber(userId,phoneNumber);
    }

    @PatchMapping("{id}/city")
    public UserModel UpdateCityNumber(@PathVariable("id") final long userId, String city)
    {
        return userService.updateCity(userId,city);
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(BadConfirmationCodeException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String getError(ExpiredCodeException exception)
    {
        return exception.getMessage();
    }
}
