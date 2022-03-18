package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("{id}")
    public UserModel getUserById(@PathVariable("id") final long userId)
    {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    @PatchMapping("{id}/update")
    public void updateUserInfo(@PathVariable("id") final long userId, @RequestBody UserUpdateRequest userUpdateRequest){
        userService.updateUserInfo(userId,userUpdateRequest);
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
