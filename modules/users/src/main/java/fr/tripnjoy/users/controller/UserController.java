package fr.tripnjoy.users.controller;

import fr.tripnjoy.users.entity.UserEntity;
import fr.tripnjoy.users.exception.BadConfirmationCodeException;
import fr.tripnjoy.users.exception.ExpiredCodeException;
import fr.tripnjoy.users.exception.ForbiddenOperationException;
import fr.tripnjoy.users.exception.UserNotFoundException;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.DeleteUserByAdminRequest;
import fr.tripnjoy.users.model.request.DeleteUserRequest;
import fr.tripnjoy.users.model.request.UserUpdateRequest;
import fr.tripnjoy.users.model.response.FirebaseTokenResponse;
import fr.tripnjoy.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public List<UserEntity> getAll()
    {
        List<UserEntity> userEntities = new ArrayList<>();
        userService.getAll().forEach(userEntities::add);
        return userEntities;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('admin')")
    public UserModel getUserById(@PathVariable("id") final long userId)
    {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    @GetMapping("me")
    public UserModel getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByEmail(authentication.getName()).orElseThrow(() -> new UserNotFoundException("Current user is not associated to a registered user"));
    }

    @PatchMapping("/update")
    @Operation(summary = "Used to update the user information")
    @ApiResponse(responseCode = "200", description = "User information have been updated")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void updateUserInfo(@RequestHeader("userId") final long userId, @RequestBody UserUpdateRequest userUpdateRequest)
    {
        userService.updateUserInfo(userId, userUpdateRequest);
    }

    @DeleteMapping("/")
    public void deleteUserAccount(@RequestHeader("userId") final long userId, @RequestBody DeleteUserRequest deleteUserRequest)
    {
        userService.deleteUserAccount(userId, deleteUserRequest);
    }

    @DeleteMapping("{id}/admin")
    public void deleteUserByAdmin(@RequestHeader("role") final String role, @PathVariable("id") final long userId,
                                  @RequestBody DeleteUserByAdminRequest deleteUserByAdminRequest)
    {
        if (!role.equals("admin"))
            throw new ForbiddenOperationException();
        userService.deleteUserByAdmin(userId, deleteUserByAdminRequest);
    }

    @GetMapping("{id}/firebase")
    @Operation(summary = "Get the firebase token of this user")
    @ApiResponse(responseCode = "200", description = "Firebase token returned. May be null")
    @ApiResponse(responseCode = "422", description = "The user does not exist")
    public FirebaseTokenResponse getFirebaseToken(@PathVariable("id") final long userId)
    {
        return new FirebaseTokenResponse(userService.getFirebaseToken(userId));
    }

    @PatchMapping("{id}/firebase")
    @Operation(summary = "Update the firebase token associated to this user. If the token is not provided, it will be unset")
    @ApiResponse(responseCode = "200", description = "Firebase token updated")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void setFirebaseToken(@PathVariable("id") final long userId, @RequestParam(name = "token", required = false) String token)
    {
        userService.setFirebaseToken(userId, token);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(BadConfirmationCodeException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String getError(ExpiredCodeException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(BadCredentialsException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
