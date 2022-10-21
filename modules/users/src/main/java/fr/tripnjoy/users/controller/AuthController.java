package fr.tripnjoy.users.controller;

import fr.tripnjoy.users.auth.TokenManager;
import fr.tripnjoy.users.exception.*;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.*;
import fr.tripnjoy.users.model.response.*;
import fr.tripnjoy.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    public static final String ERROR_RESPONSE_MSG = "Error on request";

    private final AuthService authService;
    private final TokenManager tokenManager;

    public AuthController(final AuthService authService, final TokenManager tokenManager)
    {
        this.authService = authService;
        this.tokenManager = tokenManager;
    }

    @PostMapping("register")
    @Operation(summary = "Create a new account. Will send a confirmation mail to the given address")
    @ApiResponse(responseCode = "200", description = "User is created")
    @ApiResponse(responseCode = "422", description = "If the email is already in use by another user")
    public AuthTokenResponse createAccount(@RequestBody UserCreationRequest model) {
        UserModel user = authService.createUser(model);
        return new AuthTokenResponse(tokenManager.generateFor(user.getEmail(), user.getId()));
    }

    @PostMapping("register/admin")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(summary = "Create a new admin account. Will send a confirmation mail to the given address")
    @ApiResponse(responseCode = "200", description = "Admin is created")
    @ApiResponse(responseCode = "422", description = "If the email is already in use by another user")
    public UserModel createAdminAccount(@RequestBody UserCreationRequest model)
    {
        return authService.createAdmin(model);
    }

    @PostMapping("{id}/resend")
    @Operation(summary = "Will send a new confirmation code to the user")
    @ApiResponse(responseCode = "200", description = "A new confirmation code has been sent")
    @ApiResponse(responseCode = "401", description = "The user is already confirmed")
    public void resendConfirmationCode(@PathVariable("id") final long userId)
    {
        authService.resendConfirmationCode(userId);
    }

    @PostMapping("login")
    @Operation(summary = "Log a user, to allow authenticated endpoints")
    @ApiResponse(responseCode = "401", description = "Authentication failed. Wrong username or password")
    @ApiResponse(responseCode = "200", description = "Authentication Succeeded. Use the given jwt in following requests")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return new LoginResponse(loginRequest.getUsername(), token);
    }

    @PostMapping("login/admin")
    @Operation(summary = "Log a user, to allow authenticated endpoints")
    @ApiResponse(responseCode = "401", description = "Authentication failed. Wrong username or password")
    @ApiResponse(responseCode = "200", description = "Authentication Succeeded. Use the given jwt in following requests")
    public LoginResponse loginAdmin(@RequestBody LoginRequest loginRequest) {
        String token = authService.loginAdmin(loginRequest.getUsername(), loginRequest.getPassword());
        return new LoginResponse(loginRequest.getUsername(), token);
    }

    @PostMapping("google")
    @Operation(summary = "Log a user, to allow authenticated endpoints")
    @ApiResponse(responseCode = "401", description = "Authentication failed. Wrong username or password")
    @ApiResponse(responseCode = "200", description = "Authentication Succeeded. Use the given jwt in following requests")
    public GoogleAuthResponse signInUpGoogle(@RequestBody GoogleRequest googleRequest)
    {
        GoogleUserResponse res = authService.signInUpGoogle(googleRequest);
        // FIXME: generate token
        //        String token = tokenManager.generateFor(res.user().getEmail(), res.user().getId());
        String token = "";
        return new GoogleAuthResponse(res.user().getEmail(), token, res.newUser());
    }


    @PatchMapping("{id}/confirmation")
    @Operation(summary = "Confirm a user's email")
    @ApiResponse(responseCode = "200", description = "User is now confirmed")
    @ApiResponse(responseCode = "403", description = "Invalid or expired confirmation code")
    public void confirmUser(@PathVariable("id") final long userId, @RequestBody ConfirmationCodeModel confirmationCode)
    {
        authService.confirmUser(userId, confirmationCode);
    }

    @PostMapping("forgot/password")
    @Operation(summary = "Used to receive a confirmation to update a password")
    @ApiResponse(responseCode = "200", description = "Email is sent to reset password")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest)
    {
        authService.forgotPassword(forgotPasswordRequest);
    }

    @Operation(summary = "Used to update the password with a confirmation code")
    @ApiResponse(responseCode = "200", description = "The password has been updated")
    @ApiResponse(responseCode = "403", description = "Invalid or expired confirmation code")
    @PatchMapping("validation/password")
    public UserIdResponse validateCodePassword(@RequestBody ValidateCodePasswordRequest validateCodePasswordRequest)
    {
        return authService.validateCodePassword(validateCodePasswordRequest);
    }

    @Operation(summary = "Used to update the password")
    @PatchMapping("{id}/password")
    @ApiResponse(responseCode = "200", description = "If the password has been updated")
    @ApiResponse(responseCode = "403", description = "If the old password is not valid")
    public void updatePassword(@PathVariable("id") final long userId, @RequestBody UpdatePasswordRequest updatePasswordRequest)
    {
        authService.updatePassword(userId, updatePasswordRequest);
    }

    @PatchMapping("{id}/email")
    @Operation(summary = "Used to ask update the user email. Returns a new jwt")
    @ApiResponse(responseCode = "200", description = "If the email has been updated")
    @ApiResponse(responseCode = "403", description = "If the given password is not valid")
    @ApiResponse(responseCode = "422", description = "If the new email does not exist or is already in use")
    public LoginResponse updateEmail(@PathVariable("id") final long userId, @RequestBody UpdateEmailRequest updateEmailRequest) {
        String token = authService.updateEmail(userId, updateEmailRequest);

        return new LoginResponse(updateEmailRequest.getNewEmail(), token);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String loginFailed(AuthenticationException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return "Invalid username or password";
    }

    @ExceptionHandler(UserCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String creationError(UserCreationException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UpdatePasswordException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(UpdatePasswordException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String expiredConfirmationCode(ExpiredCodeException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String badCredentials(BadCredentialsException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String badConfirmationCode(BadConfirmationCodeException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String userNotFoundException(UserNotFoundException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UserAlreadyConfirmedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String userAlreadyConfirmedException(UserAlreadyConfirmedException exception)
    {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String creationError(ForbiddenOperationException exception) {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UpdateEmailException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UpdateEmailException exception) {
        logger.debug(ERROR_RESPONSE_MSG, exception);
        return exception.getMessage();
    }
}
