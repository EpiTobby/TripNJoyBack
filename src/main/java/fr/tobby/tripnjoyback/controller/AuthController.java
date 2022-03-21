package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.exception.auth.UpdatePasswordException;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserCreationRequest;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordRequest;
import fr.tobby.tripnjoyback.model.request.UpdateEmailRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordRequest;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordRequest;
import fr.tobby.tripnjoyback.model.request.auth.LoginRequest;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.model.response.auth.AuthTokenResponse;
import fr.tobby.tripnjoyback.model.response.auth.LoginResponse;
import fr.tobby.tripnjoyback.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenManager tokenManager;

    public AuthController(final AuthService authService, final TokenManager tokenManager)
    {
        this.authService = authService;
        this.tokenManager = tokenManager;
    }

    @PostMapping("register")
    @ApiOperation(value = "Create a new account. Will send a confirmation mail to the given address")
    @ApiResponse(responseCode = "200", description = "User is created")
    @ApiResponse(responseCode = "422", description = "If the email is already in use by another user")
    public AuthTokenResponse create(@RequestBody UserCreationRequest model)
    {
        UserModel user = authService.createUser(model);
        return new AuthTokenResponse(tokenManager.generateFor(user.getEmail(), user.getId()));
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
    @ApiOperation("Confirm a user's email")
    @ApiResponse(responseCode = "200", description = "User is now confirmed")
    @ApiResponse(responseCode = "403", description = "Invalid or expired confirmation code")
    public void confirmUser(@PathVariable("id") final long userId, @RequestBody ConfirmationCodeModel confirmationCode)
    {
        authService.confirmUser(userId, confirmationCode);
    }

    @PostMapping("forgotpassword")
    @ApiOperation("Used to receive a confirmation to update a password")
    @ApiResponse(responseCode = "200", description = "Email is sent to reset password")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest)
    {
        authService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("validatepassword")
    @ApiOperation("Used to update the password with a confirmation code")
    @ApiResponse(responseCode = "200", description = "The password has been updated")
    @ApiResponse(responseCode = "403", description = "Invalid or expired confirmation code")
    @PatchMapping("validatecodepassword")
    public UserIdResponse validateCodePassword(@RequestBody ValidateCodePasswordRequest validateCodePasswordRequest)
    {
        return authService.validateCodePassword(validateCodePasswordRequest);
    }

    @PatchMapping("{id}/updatepassword")
    @ApiOperation("Used to update the password")
    @ApiResponse(responseCode = "200", description = "If the password has been updated")
    @ApiResponse(responseCode = "403", description = "If the old password is not valid")
    public void updatePassword(@PathVariable("id") final long userId,@RequestBody UpdatePasswordRequest updatePasswordRequest)
    {
        authService.updatePassword(userId,updatePasswordRequest);
    }

    @PatchMapping("{id}/updateemail")
    @ApiOperation("Used to ask update the user email")
    @ApiResponse(responseCode = "200", description = "If the email has been updated")
    @ApiResponse(responseCode = "403", description = "If the given password is not valid")
    @ApiResponse(responseCode = "422", description = "If the new email does not exist")
    public void updateEmail(@PathVariable("id") final long userId, @RequestBody UpdateEmailRequest updateEmailRequest){
        authService.updateEmail(userId, updateEmailRequest);
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

    @ExceptionHandler(UpdatePasswordException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(UpdatePasswordException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(UpdateEmailException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UpdateEmailException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String expiredConfirmationCode(ExpiredCodeException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String badCredentials(BadCredentialsException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String badConfirmationCode(BadConfirmationCodeException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String badConfirmationCode(UserNotFoundException exception)
    {
        return exception.getMessage();
    }
}
