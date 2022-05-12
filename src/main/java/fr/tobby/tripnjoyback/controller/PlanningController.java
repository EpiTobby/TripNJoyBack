package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import fr.tobby.tripnjoyback.service.PlanningService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public ActivityModel createActivity(@PathVariable(name = "groupId") final long groupId, final CreateActivityRequest request)
    {
        if (!idCheckerService.isUserInGroup(idCheckerService.getCurrentUserId(), groupId))
            throw new ForbiddenOperationException("You are not allowed to view this group");
        return this.service.createActivity(groupId, request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public String onForbiddenOperationException(ForbiddenOperationException exception)
    {
        return exception.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String onEntityNotFound(EntityNotFoundException exception)
    {
        return exception.getMessage();
    }
}
