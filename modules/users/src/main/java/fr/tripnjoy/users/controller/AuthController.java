package fr.tripnjoy.users.controller;

import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.users.api.request.CheckJwtRequest;
import fr.tripnjoy.users.api.response.CheckJwtResponse;
import fr.tripnjoy.users.api.response.JwtUserDetails;
import fr.tripnjoy.users.auth.TokenManager;
import fr.tripnjoy.users.exception.TokenVerificationException;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.*;
import fr.tripnjoy.users.model.response.*;
import fr.tripnjoy.users.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private void checkIsAdmin(List<String> roles) throws UnauthorizedException
    {
        if (!roles.contains("admin"))
            throw new UnauthorizedException("This resource is accessible only to administrators");
    }

    @PostMapping("jwtcheck")
    public CheckJwtResponse checkJwt(@RequestBody CheckJwtRequest request)
    {
        try
        {
            JwtUserDetails jwtUserDetails = tokenManager.verifyToken(request.getJwt());
            return new CheckJwtResponse(true, jwtUserDetails);
        }
        catch (TokenVerificationException e)
        {
            return new CheckJwtResponse(false, null);
        }
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
    @Operation(summary = "Create a new admin account. Will send a confirmation mail to the given address")
    @ApiResponse(responseCode = "200", description = "Admin is created")
    @ApiResponse(responseCode = "422", description = "If the email is already in use by another user")
    public UserModel createAdminAccount(@RequestHeader("roles") List<String> roles,
                                        @RequestBody UserCreationRequest model) throws UnauthorizedException
    {
        checkIsAdmin(roles);
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
        String token = tokenManager.generateFor(res.user().getEmail(), res.user().getId());
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
}