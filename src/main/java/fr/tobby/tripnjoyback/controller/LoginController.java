package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.auth.LoginRequest;
import fr.tobby.tripnjoyback.model.response.auth.LoginResponse;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;

    public LoginController(final UserService userService,
                           final AuthenticationManager authenticationManager, final TokenManager tokenManager,
                           final UserDetailsService userDetailsService)
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("create")
    public UserModel create(@RequestBody UserCreationModel model)
    {
        return userService.createUser(model);
    }

    @PostMapping("")
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        if (!authenticate.isAuthenticated())
            return LoginResponse.FAILED;
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = tokenManager.generateFor(userDetails);

        return new LoginResponse(userDetails.getUsername(), token, true);
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }
}
