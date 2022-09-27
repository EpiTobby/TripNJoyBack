package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.request.UpdateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import fr.tobby.tripnjoyback.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/{groupId}/planning")
@CrossOrigin
public class PlanningController {

    private final PlanningService service;
    private final IdCheckerService idCheckerService;

    public PlanningController(final PlanningService service, final IdCheckerService idCheckerService)
    {
        this.service = service;
        this.idCheckerService = idCheckerService;
    }

    @PostMapping
    @Operation(summary = "Add a new activity to the group's planning")
    @ApiResponse(responseCode = "200", description = "Activity created")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The group does not exist")
    public ActivityModel createActivity(@PathVariable(name = "groupId") final long groupId, @RequestBody final CreateActivityRequest request)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        ActivityModel activity = this.service.createActivity(groupId, request);
        this.service.joinActivity(activity.id(), idCheckerService.getCurrentUserId());
        return activity;
    }

    @GetMapping
    @Operation(summary = "Get the group's activities")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The group does not exist")
    public List<ActivityModel> getActivities(@PathVariable(name = "groupId") final long groupId)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
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
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        if (!idCheckerService.isUserInGroup(userId, groupId))
            throw new ForbiddenOperationException();
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
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        if (!idCheckerService.isUserInGroup(userId, groupId))
            throw new ForbiddenOperationException();
        this.service.leaveActivity(activityId, userId);
    }

    @DeleteMapping("{activityId}")
    @Operation(summary = "Delete the activity")
    @ApiResponse(responseCode = "204", description = "Activity deleted")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable(name = "groupId") final long groupId,
                               @PathVariable(name = "activityId") final long activityId)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        this.service.deleteActivity(activityId);
    }

    @PatchMapping("{activityId}")
    @Operation(summary = "Update the activity")
    @ApiResponse(responseCode = "200", description = "Activity updated. New activity is returned")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    public ActivityModel updateActivity(@PathVariable(name = "groupId") final long groupId,
                                        @PathVariable(name = "activityId") final long activityId,
                                        @NotNull @RequestBody final UpdateActivityRequest updateActivityRequest)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
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
