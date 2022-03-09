package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "users")
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
    public UserEntity create(@RequestBody UserCreationModel model)
    {
        return userService.createUser(model);
    }

    @GetMapping("{id}")
    public UserEntity getUserById(@PathVariable("id") final long userId)
    {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    @PatchMapping("{id}/phone")
    public UserEntity UpdatePhoneNumber(@PathVariable("id") final long userId, String phoneNumber)
    {
        return userService.updatePhoneNumber(userId,phoneNumber);
    }

    @PatchMapping("{id}/city")
    public UserEntity UpdateCityNumber(@PathVariable("id") final long userId, String city)
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
}
