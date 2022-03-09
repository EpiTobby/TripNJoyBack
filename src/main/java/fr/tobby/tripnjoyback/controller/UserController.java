package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
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
    public UserEntity create(UserCreationModel model)
    {
        return userService.createUser(model);
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }
}
