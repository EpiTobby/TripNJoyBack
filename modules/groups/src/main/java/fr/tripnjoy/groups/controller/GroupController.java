package fr.tripnjoy.groups.controller;

import fr.tripnjoy.common.dto.ModelWithEmail;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.exception.*;
import fr.tripnjoy.groups.model.GroupModel;
import fr.tripnjoy.groups.model.request.*;
import fr.tripnjoy.groups.model.response.GroupInfoModel;
import fr.tripnjoy.groups.model.response.GroupMemberModel;
import fr.tripnjoy.groups.model.response.GroupMemoriesResponse;
import fr.tripnjoy.groups.service.GroupService;
import fr.tripnjoy.users.api.exception.UserNotConfirmedException;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "groups")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the group of the user")
    @ApiResponse(responseCode = "200", description = "Return the list of groups the user is in")
    public Collection<GroupModel> getUserGroups(@PathVariable("id") final long userId) {
        return groupService.getUserGroups(userId);
    }

    @GetMapping("invites/{id}")
    @Operation(summary = "Get all the group invitation of the user")
    @ApiResponse(responseCode = "200", description = "Return the list of groups the user is invited to")
    public Collection<GroupModel> getUserInvites(@PathVariable("id") final long userId) {
        return groupService.getUserInvites(userId);
    }

    @GetMapping("info/{id}")
    @Operation(summary = "Get info about a group")
    public GroupInfoModel getInfo(@PathVariable("id") final long groupId) {
        return groupService.getGroupInfo(groupId)
                           .orElseThrow(GroupNotFoundException::new);
    }

    private void checkOwnership(long groupId, String username) {
        String ownerEmail = groupService.getOwnerEmail(groupId);
        if (!ownerEmail.equals(username))
            throw new ForbiddenOperationException("You cannot perform this operation");
    }

    @DeleteMapping("{group}/user/")
    @Operation(summary = "Remove the user from a group")
    @ApiResponse(responseCode = "200", description = "The user has left the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void leaveGroup(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId) {
        groupService.removeUserFromGroup(groupId, userId);
    }

    @GetMapping("{groupId}/users/{userId}")
    @Operation(summary = "Get the information related to the member")
    @ApiResponse(responseCode = "200", description = "User information")
    @ApiResponse(responseCode = "422", description = "User or group not found")
    @ApiResponse(responseCode = "403", description = "You are not allowed to view members of this group")
    public GroupMemberModel getMember(@PathVariable("groupId") final long groupId, @PathVariable("userId") final long userId,
                                      @RequestHeader("userId") final long currentUserId) {
        if (!groupService.isInGroup(groupId, currentUserId))
            throw new ForbiddenOperationException("You are not a member of this group");
        return groupService.getMember(groupId, userId);
    }

    @PostMapping("private/")
    @Operation(summary = "Create a private group")
    @ApiResponse(responseCode = "200", description = "Returns the created group")
    @ApiResponse(responseCode = "422", description = "User or Group does not exist")
    public GroupModel createPrivateGroup(@RequestHeader("userId") final long userId, @RequestBody CreatePrivateGroupRequest createPrivateGroupRequest) {
        return groupService.createPrivateGroup(userId, createPrivateGroupRequest);
    }

    @PostMapping("private/{group}/user")
    @Operation(summary = "Add user to private group")
    @ApiResponse(responseCode = "200", description = "The user is added to the group")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void inviteUserInPrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @RequestBody ModelWithEmail model) {
        checkOwnership(groupId, username);
        groupService.inviteUserInPrivateGroup(groupId, model.getEmail());
    }

    @DeleteMapping("private/{group}/user/{id}")
    @Operation(summary = "Remove user from private group")
    @ApiResponse(responseCode = "200", description = "The user is removed")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void removeUserFromPrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @PathVariable("id") final long userId) {
        checkOwnership(groupId, username);
        groupService.removeUserFromGroup(groupId, userId);
    }

    @PatchMapping("private/{group}")
    @Operation(summary = "Update the private group")
    @ApiResponse(responseCode = "200", description = "The group is updated")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void updatePrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username, @RequestBody UpdatePrivateGroupRequest updatePrivateGroupRequest) {
        checkOwnership(groupId, username);
        groupService.updatePrivateGroup(groupId, updatePrivateGroupRequest);
    }

    @PatchMapping("{group}")
    @Operation(summary = "Update the public group")
    @ApiResponse(responseCode = "200", description = "The group is updated")
    @ApiResponse(responseCode = "422", description = "Group does not exist")
    public void updatePublicGroup(@PathVariable("group") final long groupId, @RequestBody UpdatePublicGroupRequest request) {
        groupService.updatePublicGroup(groupId, request);
    }

    @DeleteMapping("private/{group}")
    @Operation(summary = "Delete the private group")
    @ApiResponse(responseCode = "200", description = "The group is deleted")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void deletePrivateGroup(@PathVariable("group") final long groupId, @RequestHeader("username") String username) {
        checkOwnership(groupId, username);
        groupService.deletePrivateGroup(groupId);
    }

    @PatchMapping("{group}/join/")
    @Operation(summary = "Accept the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has joined the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void joinGroup(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId) {
        groupService.joinGroup(groupId, userId);
    }

    @PatchMapping("private/{group}/join/")
    @Operation(summary = "Accept the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has joined the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void joinGroupWithoutInvite(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId, @RequestBody JoinGroupWithoutInviteModel model) {
        groupService.joinGroupWithoutInvite(groupId, userId, model);
    }

    @PatchMapping("{group}/decline/")
    @Operation(summary = "Decline the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has declined the invite")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void declineGroupInvite(@PathVariable("group") final long groupId, @RequestHeader("userId") final long userId) {
        groupService.declineGroupInvite(groupId, userId);
    }

    /*
    FIXME: profile service
    @Operation(summary = "Make a private group public")
    @ApiResponse(responseCode = "200", description = "The group is now public")
    @ApiResponse(responseCode = "422", description = "Group does not exist")
    @ApiResponse(responseCode = "403", description = "User is not in the group, or the group is already public")
    @PatchMapping("private/{groupId}/public")
    public void setGroupPublic(@PathVariable("groupId") final long groupId, @RequestBody ProfileCreationRequest profile) {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        try {
            groupService.setGroupPublic(groupId, profile);
        } catch (IllegalArgumentException e) {
            throw new ForbiddenOperationException("This group is already public");
        }
    }
     */

    @Operation(summary = "Get all the memories from a group")
    @ApiResponse(responseCode = "200", description = "The memories are returned")
    @ApiResponse(responseCode = "422", description = "Group does not exist")
    @GetMapping("{groupId}/memories")
    public GroupMemoriesResponse getMemories(@PathVariable("groupId") final long groupId) {
        return groupService.getAllMemories(groupId);
    }

    @Operation(summary = "Add memory to a group")
    @ApiResponse(responseCode = "200", description = "The memory is added to the group")
    @ApiResponse(responseCode = "422", description = "Group or Memory does not exist")
    @PostMapping("{groupId}/memories")
    public GroupMemoriesResponse addMemory(@PathVariable("groupId") final long groupId, @RequestBody GroupMemoryRequest memoryCreationRequest) {
        return groupService.addMemory(groupId, memoryCreationRequest.getMemoryUrl());
    }

    @GetMapping("private/{group}/qrcode")
    @Operation(summary = "Get the QR code to join a private group")
    @ApiResponse(responseCode = "200", description = "Returns the encoded qr code")
    public String getQRCode(@PathVariable("group") final long groupId, @RequestHeader("userId") long userId) {
        if (!groupService.isInGroup(groupId, userId))
            throw new ForbiddenOperationException();
        return groupService.getQRCode(groupId);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UserNotConfirmedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotConfirmedException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage() != null ? exception.getMessage() : "You are not authorized to perform this operation";
    }

    @ExceptionHandler(UpdateGroupException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UpdateGroupException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(JoinGroupFailedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(JoinGroupFailedException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(GroupCreationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(GroupCreationException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UserAlreadyInGroupException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserAlreadyInGroupException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(GroupNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(GroupNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

}
