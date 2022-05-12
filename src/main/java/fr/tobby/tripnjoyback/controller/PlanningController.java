package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import fr.tobby.tripnjoyback.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/{groupId}/planning")
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
    public ActivityModel createActivity(@PathVariable(name = "groupId") final long groupId, final CreateActivityRequest request)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        return this.service.createActivity(groupId, request);
    }

    @GetMapping
    @Operation(summary = "Add the current user to the given activity")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The group does not exist")
    public List<ActivityModel> getActivities(@PathVariable(name = "groupId") final long groupId)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        return this.service.getGroupActivities(groupId);
    }

    @PatchMapping("{activityId}")
    @Operation(summary = "Add the current user to the given activity")
    @ApiResponse(responseCode = "204", description = "User added")
    @ApiResponse(responseCode = "403", description = "User does not belong to the group")
    @ApiResponse(responseCode = "404", description = "The activity or group does not exist")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinActivity(@PathVariable(name = "groupId") final long groupId,
                             @PathVariable(name = "activityId") final long activityId)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException();
        this.service.joinActivity(activityId, idCheckerService.getCurrentUserId());
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
