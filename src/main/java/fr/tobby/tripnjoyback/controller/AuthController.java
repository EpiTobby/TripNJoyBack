package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordRequest;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordRequest;
import fr.tobby.tripnjoyback.model.request.auth.LoginRequest;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.model.response.auth.LoginResponse;
import fr.tobby.tripnjoyback.service.AuthService;
import fr.tobby.tripnjoyback.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;

    public AuthController(final UserService userService,
                          final AuthenticationManager authenticationManager, final TokenManager tokenManager,
                          final UserDetailsService userDetailsService, final AuthService authService)
    {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
    }

    @PostMapping("register")
    public UserModel create(@RequestBody UserCreationModel model)
    {
        return authService.createUser(model);
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        UserModel userModel = userService.findByEmail(loginRequest.getUsername()).orElseThrow();
        String token = tokenManager.generateFor(userDetails, userModel.getId());

        return new LoginResponse(userDetails.getUsername(), token);
    }

    @PatchMapping("{id}/confirm")
    public boolean confirmUser(@PathVariable("id") final long userId, @RequestBody ConfirmationCodeModel confirmationCode)
    {
        return authService.confirmUser(userId,confirmationCode);
    }

    @PostMapping("forgotpassword")
    public void forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest)
    {
        authService.forgotPassword(forgotPasswordRequest);
    }

    @PatchMapping("validatecodepassword")
    public UserIdResponse validateCodePassword(@RequestBody ValidateCodePasswordRequest validateCodePasswordRequest)
    {
        return authService.validateCodePassword(validateCodePasswordRequest);
    }

    @PatchMapping("{id}/updatepassword")
    public void UpdatePassword(@PathVariable("id") final long userId, @RequestBody UpdatePasswordRequest updatePasswordRequest)
    {
        authService.updatePassword(userId, updatePasswordRequest);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String loginFailed(AuthenticationException exception)
    {
        return "Invalid username or password";
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        return exception.getMessage();
    }
}
