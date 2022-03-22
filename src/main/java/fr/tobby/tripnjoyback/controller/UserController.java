package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.DeleteUserByAdminRequest;
import fr.tobby.tripnjoyback.model.request.DeleteUserRequest;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('admin')")
    public Iterable<UserEntity> getAll()
    {
        return userService.getAll();
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

    @PatchMapping("{id}/update")
    @ApiOperation("Used to update the user information")
    @ApiResponse(responseCode = "200", description = "User information have been updated")
    @ApiResponse(responseCode = "422", description = "If the user does not exist")
    public void updateUserInfo(@PathVariable("id") final long userId, @RequestBody UserUpdateRequest userUpdateRequest){
        userService.updateUserInfo(userId,userUpdateRequest);
    }

    @DeleteMapping("{id}")
    public void deleteUserAccount(@PathVariable("id") final long userId, @RequestBody DeleteUserRequest deleteUserRequest){
        userService.deleteUserAccount(userId, deleteUserRequest);
    }

    @DeleteMapping("{id}/admin")
    @PreAuthorize("hasAuthority('admin')")
    public void deleteUserAdmin(@PathVariable("id") final long userId, @RequestBody DeleteUserByAdminRequest deleteUserByAdminRequest)
    {
         userService.deleteUserByAdmin(userId, deleteUserByAdminRequest);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(BadConfirmationCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(BadConfirmationCodeException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String getError(ExpiredCodeException exception)
    {
        return exception.getMessage();
    }
}
