package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.DeleteUserByAdminRequest;
import fr.tobby.tripnjoyback.model.request.DeleteUserRequest;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import fr.tobby.tripnjoyback.service.UserService;
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
@CrossOrigin
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final IdCheckerService idCheckerService;

    public UserController(UserService userService, final IdCheckerService idCheckerService) {
        this.userService = userService;
        this.idCheckerService = idCheckerService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('admin')")
    @ResponseStatus(code = HttpStatus.PARTIAL_CONTENT)
    public List<UserModel> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('admin')")
    public UserModel getUserById(@PathVariable("id") final long userId) {
        return userService.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
    }

    @GetMapping("me")
    public UserModel getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByEmail(authentication.getName()).orElseThrow(() -> new UserNotFoundException("Current user is not associated to a registered user"));
    }

    @PatchMapping("{id}/update")
    @Operation(summary = "Used to update the user information")
    @ApiResponse(responseCode = "200", description = "User information have been updated")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void updateUserInfo(@PathVariable("id") final long userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        idCheckerService.checkId(userId);
        userService.updateUserInfo(userId, userUpdateRequest);
    }

    @DeleteMapping("{id}")
    public void deleteUserAccount(@PathVariable("id") final long userId, @RequestBody DeleteUserRequest deleteUserRequest) {
        idCheckerService.checkId(userId);
        userService.deleteUserAccount(userId, deleteUserRequest);
    }

    @DeleteMapping("{id}/admin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteUserByAdmin(@PathVariable("id") final long userId, @RequestBody DeleteUserByAdminRequest deleteUserByAdminRequest) {
        userService.deleteUserByAdmin(userId, deleteUserByAdminRequest);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(BadConfirmationCodeException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String getError(ExpiredCodeException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(BadCredentialsException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
