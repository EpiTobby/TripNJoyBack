package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserCreationRequest;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordRequest;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordRequest;
import fr.tobby.tripnjoyback.model.request.auth.LoginRequest;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.model.response.auth.LoginResponse;
import fr.tobby.tripnjoyback.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("register")
    @ApiOperation(value = "Create a new account. Will send a confirmation mail to the given address")
    @ApiResponse(responseCode = "200", description = "User is created")
    @ApiResponse(responseCode = "422", description = "If the email is already in use by another user")
    public UserModel create(@RequestBody UserCreationRequest model)
    {
        return authService.createUser(model);
    }

    @PostMapping("login")
    @ApiOperation("Log a user, to allow authenticated endpoints")
    @ApiResponse(responseCode = "401", description = "Authentication failed. Wrong username or password")
    @ApiResponse(responseCode = "200", description = "Authentication Succeeded. Use the given jwt in following requests")
    public LoginResponse login(@RequestBody LoginRequest loginRequest)
    {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

        return new LoginResponse(loginRequest.getUsername(), token);
    }

    @PatchMapping("{id}/confirm")
    public boolean confirmUser(@PathVariable("id") final long userId, @RequestBody ConfirmationCodeModel confirmationCode)
    {
        return authService.confirmUser(userId, confirmationCode);
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
