package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.JoinGroupWithoutInviteModel;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePublicGroupRequest;
import fr.tobby.tripnjoyback.model.response.GroupInfoModel;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
import fr.tobby.tripnjoyback.service.GroupService;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "groups")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;
    private final IdCheckerService idCheckerService;

    public GroupController(GroupService groupService, final IdCheckerService idCheckerService)
    {
        this.groupService = groupService;
        this.idCheckerService = idCheckerService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the group of the user")
    @ApiResponse(responseCode = "200", description = "Return the list of groups the user is in")
    public Collection<GroupModel> getUserGroups(@PathVariable("id") final long userId)
    {
        return groupService.getUserGroups(userId);
    }

    @GetMapping("invites/{id}")
    @Operation(summary = "Get all the group invitation of the user")
    @ApiResponse(responseCode = "200", description = "Return the list of groups the user is invited to")
    public Collection<GroupModel> getUserInvites(@PathVariable("id") final long userId)
    {
        return groupService.getUserInvites(userId);
    }

    @GetMapping("info/{id}")
    @Operation(summary = "Get info about a group")
    public GroupInfoModel getInfo(@PathVariable("id") final long groupId)
    {
        return groupService.getGroup(groupId)
                           .map(GroupInfoModel::of)
                           .orElseThrow(GroupNotFoundException::new);
    }

    private void checkOwnership(long groupId, Authentication authentication) {
        String ownerEmail = groupService.getOwnerEmail(groupId);
        if (!ownerEmail.equals(authentication.getName()))
            throw new ForbiddenOperationException("You cannot perform this operation");
    }

    @DeleteMapping("{group}/user/{id}")
    @Operation(summary = "Remove the user from a group")
    @ApiResponse(responseCode = "200", description = "The user has left the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void leaveGroup(@PathVariable("group") final long groupId, @PathVariable("id") final long userId)
    {
        idCheckerService.checkId(userId);
        groupService.removeUserFromGroup(groupId, userId);
    }

    @GetMapping("{groupId}/users/{userId}")
    @Operation(summary = "Get the information related to the member")
    @ApiResponse(responseCode = "200", description = "User information")
    @ApiResponse(responseCode = "422", description = "User or group not found")
    @ApiResponse(responseCode = "403", description = "You are not allowed to view members of this group")
    public GroupMemberModel getMember(@PathVariable("groupId") final long groupId, @PathVariable("userId") final long userId)
    {
        if (!groupService.isInGroup(groupId, idCheckerService.getCurrentUserId()))
            throw new ForbiddenOperationException("You are not a member of this group");
        return groupService.getMember(groupId, userId);
    }

    @PostMapping("private/{id}")
    @Operation(summary = "Create a private group")
    @ApiResponse(responseCode = "200", description = "Returns the created group")
    @ApiResponse(responseCode = "422", description = "User or Group does not exist")
    public GroupModel createPrivateGroup(@PathVariable("id") final long userId, @RequestBody CreatePrivateGroupRequest createPrivateGroupRequest) {
        idCheckerService.checkId(userId);
        return groupService.createPrivateGroup(userId, createPrivateGroupRequest);
    }

    @PostMapping("private/{group}/user")
    @Operation(summary = "Add user to private group")
    @ApiResponse(responseCode = "200", description = "The user is added to the group")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void inviteUserInPrivateGroup(@PathVariable("group") final long groupId, @RequestBody ModelWithEmail model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkOwnership(groupId, authentication);
        groupService.inviteUserInPrivateGroup(groupId, model.getEmail());
    }

    @DeleteMapping("private/{group}/user/{id}")
    @Operation(summary = "Remove user from private group")
    @ApiResponse(responseCode = "200", description = "The user is removed")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void removeUserFromPrivateGroup(@PathVariable("group") final long groupId, @PathVariable("id") final long userId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkOwnership(groupId, authentication);
        groupService.removeUserFromGroup(groupId, userId);
    }

    @PatchMapping("private/{group}")
    @Operation(summary = "Update the private group")
    @ApiResponse(responseCode = "200", description = "The group is updated")
    @ApiResponse(responseCode = "403", description = "The client is not the owner of the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void updatePrivateGroup(@PathVariable("group") final long groupId, @RequestBody UpdatePrivateGroupRequest updatePrivateGroupRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkOwnership(groupId, authentication);
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
    public void deletePrivateGroup(@PathVariable("group") final long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        checkOwnership(groupId, authentication);
        groupService.deletePrivateGroup(groupId);
    }

    @PatchMapping("{group}/join/{id}")
    @Operation(summary = "Accept the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has joined the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void joinGroup(@PathVariable("group") final long groupId, @PathVariable("id") final long userId) {
        idCheckerService.checkId(userId);
        groupService.joinGroup(groupId, userId);
    }

    @PatchMapping("private/{group}/join/{id}")
    @Operation(summary = "Accept the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has joined the group")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void joinGroupWithoutInvite(@PathVariable("group") final long groupId, @PathVariable("id") final long userId, @RequestBody JoinGroupWithoutInviteModel model) {
        idCheckerService.checkId(userId);
        groupService.joinGroupWithoutInvite(groupId, userId, model);
    }

    @PatchMapping("{group}/decline/{id}")
    @Operation(summary = "Decline the invitation to the group")
    @ApiResponse(responseCode = "200", description = "The user has declined the invite")
    @ApiResponse(responseCode = "422", description = "Group or User does not exist")
    public void declineGroupInvite(@PathVariable("group") final long groupId, @PathVariable("id") final long userId) {
        idCheckerService.checkId(userId);
        groupService.declineGroupInvite(groupId, userId);
    }

    @Operation(summary = "Make a private group public")
    @ApiResponse(responseCode = "200", description = "The group is now public")
    @ApiResponse(responseCode = "422", description = "Group does not exist")
    @ApiResponse(responseCode = "403", description = "User is not in the group, or the group is already public")
    @PatchMapping("private/{groupId}/public")
    public void setGroupPublic(@PathVariable("groupId") final long groupId, @RequestBody ProfileCreationRequest profile)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        try
        {
            groupService.setGroupPublic(groupId, profile);
        }
        catch (IllegalArgumentException e)
        {
            throw new ForbiddenOperationException("This group is already public");
        }
    }

    @GetMapping("private/{group}/qrcode")
    @Operation(summary = "Get the QR code to join a private group")
    @ApiResponse(responseCode = "200", description = "Returns the encoded qr code")
    public String getQRCode(@PathVariable("group") final long groupId) {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
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
