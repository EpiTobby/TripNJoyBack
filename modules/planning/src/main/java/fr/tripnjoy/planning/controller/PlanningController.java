package fr.tripnjoy.planning.controller;


import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.planning.dto.request.CreateActivityRequest;
import fr.tripnjoy.planning.dto.request.UpdateActivityRequest;
import fr.tripnjoy.planning.dto.response.ActivityResponse;
import fr.tripnjoy.planning.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/{groupId}/planning")
public class PlanningController {

    private final PlanningService service;
    private final GroupFeignClient groupFeignClient;

    public PlanningController(final PlanningService service, final GroupFeignClient groupFeignClient)
    {
        this.service = service;
        this.groupFeignClient = groupFeignClient;
    }

    private void checkUserIsInGroup(long userId, long groupId)
    {
        boolean isInGroup = groupFeignClient.getUserGroups(userId)
                                            .stream()
                                            .anyMatch(group -> group.getId() == groupId);
        if (!isInGroup)
            throw new UnauthorizedException();
    }

    @PostMapping
    @Operation(summary = "Add a new activity to the group's planning")
    @ApiResponse(responseCode = "200", description = "Activity created")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The group does not exist")
    public ActivityResponse createActivity(@RequestHeader("userId") long userId, @PathVariable(name = "groupId") final long groupId,
                                           @RequestBody final CreateActivityRequest request)
    {
        checkUserIsInGroup(userId, groupId);
        ActivityResponse activity = this.service.createActivity(groupId, request);
        this.service.joinActivity(activity.id(), userId);
        return activity;
    }

    @GetMapping
    @Operation(summary = "Get the group's activities")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The group does not exist")
    public List<ActivityResponse> getActivities(@RequestHeader("userId") long userId, @PathVariable(name = "groupId") final long groupId)
    {
        checkUserIsInGroup(userId, groupId);
        return this.service.getGroupActivities(groupId);
    }

    @PatchMapping("{activityId}/join")
    @Operation(summary = "Add the user to the given activity")
    @ApiResponse(responseCode = "204", description = "User added")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinActivity(@PathVariable(name = "groupId") final long groupId,
                             @PathVariable(name = "activityId") final long activityId,
                             @RequestParam(name = "userId") final long userId)
    {
        checkUserIsInGroup(userId, groupId);
        this.service.joinActivity(activityId, userId);
    }

    @PatchMapping("{activityId}/leave")
    @Operation(summary = "Remove the user from the given activity")
    @ApiResponse(responseCode = "204", description = "User removed")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveActivity(@PathVariable(name = "groupId") final long groupId,
                              @PathVariable(name = "activityId") final long activityId,
                              @RequestParam(name = "userId") final long userId)
    {
        checkUserIsInGroup(userId, groupId);
        this.service.leaveActivity(activityId, userId);
    }

    @DeleteMapping("{activityId}")
    @Operation(summary = "Delete the activity")
    @ApiResponse(responseCode = "204", description = "Activity deleted")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@RequestHeader("userId") long userId,
                               @PathVariable(name = "groupId") final long groupId,
                               @PathVariable(name = "activityId") final long activityId)
    {
        checkUserIsInGroup(userId, groupId);
        this.service.deleteActivity(activityId);
    }

    @PatchMapping("{activityId}")
    @Operation(summary = "Update the activity")
    @ApiResponse(responseCode = "200", description = "Activity updated. New activity is returned")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    public ActivityResponse updateActivity(@RequestHeader("userId") long userId,
                                           @PathVariable(name = "groupId") final long groupId,
                                           @PathVariable(name = "activityId") final long activityId,
                                           @NotNull @RequestBody final UpdateActivityRequest updateActivityRequest)
    {
        checkUserIsInGroup(userId, groupId);
        return this.service.updateActivity(activityId, updateActivityRequest);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String onForbiddenOperationException(ForbiddenOperationException exception)
    {
        return exception.getMessage() == null
               ? "You are not authorized to perform this operation"
               : exception.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String onEntityNotFound(EntityNotFoundException exception)
    {
        return exception.getMessage();
    }
}
