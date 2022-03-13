package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public LoginController(final UserService userService,
                           final AuthenticationManager authenticationManager)
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("create")
    public UserModel create(@RequestBody UserCreationModel model)
    {
        return userService.createUser(model);
    }

    @PostMapping("")
    public Authentication login(@RequestParam("username") String username, @RequestParam("password") String password)
    {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authenticate.isAuthenticated())
        {
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }
        return authenticate;
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }
}
