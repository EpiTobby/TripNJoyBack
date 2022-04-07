package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdateGroupRequest;
import fr.tobby.tripnjoyback.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "groups")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;

    public GroupController(GroupService groupService){ this.groupService = groupService;}

    @GetMapping("{id}")
    @Operation(summary = "Get all the group of the user")
    private List<GroupModel> getUserGroups(@PathVariable("id") final long userId){
        return groupService.getUserGroups(userId);
    }

    @PostMapping("private/create/{id}")
    @Operation(summary = "Create a private group")
    @ApiResponse(responseCode = "200", description = "Returns the created group")
    @ApiResponse(responseCode = "422", description = "User or Group does not exist")
    public GroupModel createPrivateGroup(@PathVariable("id") final long userId, CreatePrivateGroupRequest createPrivateGroupRequest){
        return groupService.createPrivateGroup(userId, createPrivateGroupRequest.getMaxSize());
    }

    @PatchMapping("private/{group}/add")
    @Operation(summary = "Add user to private group")
    @ApiResponse(responseCode = "200", description = "The user is added to the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void addUserToPrivateGroup(@PathVariable("group") final long groupId, @RequestBody ModelWithEmail model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        groupService.addUserToPrivateGroup(groupId, model.getEmail(), authentication);
    }

    @DeleteMapping("private/{group}/remove")
    @Operation(summary = "Remove user from private group")
    @ApiResponse(responseCode = "200", description = "The user is removed")
    @ApiResponse(responseCode = "422", description = "")
    public void RemoveUserFromPrivateGroup(@PathVariable("group") final long groupId, @RequestBody ModelWithEmail model){
        groupService.removeUserOfGroup(groupId, model.getEmail());
    }

    @PatchMapping("private/{group}/update")
    @Operation(summary = "Update the private group")
    @ApiResponse(responseCode = "200", description = "The group is updated")
    @ApiResponse(responseCode = "422", description = "")
    public void UpdatePrivateGroup(@PathVariable("group") final long groupId, @RequestBody UpdateGroupRequest updateGroupRequest){
        groupService.UpdatePrivateGroup(groupId, updateGroupRequest);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotConfirmedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotConfirmedException exception)
    {
        logger.debug("Error on request", exception);
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

    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(GroupNotFoundException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

}
