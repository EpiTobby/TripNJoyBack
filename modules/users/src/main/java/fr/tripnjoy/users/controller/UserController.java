package fr.tripnjoy.users.controller;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import fr.tripnjoy.users.api.response.UserResponse;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.DeleteUserByAdminRequest;
import fr.tripnjoy.users.model.request.DeleteUserRequest;
import fr.tripnjoy.users.model.request.UserUpdateRequest;
import fr.tripnjoy.users.model.response.FirebaseTokenResponse;
import fr.tripnjoy.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    private void checkIsAdmin(List<String> roles) throws UnauthorizedException
    {
        if (!roles.contains("admin"))
            throw new UnauthorizedException("This resource is accessible only to administrators");
    }

    @GetMapping("")
    public List<UserResponse> getAll(@RequestHeader("roles") List<String> roles) throws UnauthorizedException
    {
        checkIsAdmin(roles);
        return userService.getAll()
                          .stream()
                          .map(UserModel::toDto)
                          .toList();
    }

    @GetMapping("{id}")
    public UserResponse getUserById(@RequestHeader("roles") List<String> roles, @PathVariable("id") final long userId) throws UnauthorizedException
    {
        checkIsAdmin(roles);
        return userService.findById(userId)
                          .orElseThrow(() -> new UserNotFoundException("No user with id " + userId))
                          .toDto();
    }

    @GetMapping("me")
    public UserResponse getCurrentUser(@RequestHeader("username") String username)
    {
        return userService.findByEmail(username)
                          .orElseThrow(() -> new UserNotFoundException("Current user is not associated to a registered user"))
                          .toDto();
    }

    @GetMapping("/{id}/exists")
    public BooleanResponse exists(@PathVariable("id") final long userId)
    {
        return new BooleanResponse(userService.findById(userId).isPresent());
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

    @GetMapping("/waiting")
    public Collection<UserResponse> getUsersWaitingForGroup()
    {
        return userService.getUsersWaitingForGroup().stream().map(UserModel::toDto).toList();
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
}
