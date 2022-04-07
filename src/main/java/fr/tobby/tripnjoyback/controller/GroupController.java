package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "groups")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;

    public GroupController(GroupService groupService){ this.groupService = groupService;}

    @PostMapping("private/create/{id}")
    @Operation(summary = "Create a private group")
    @ApiResponse(responseCode = "200", description = "Returns the created group")
    @ApiResponse(responseCode = "422", description = "")
    public GroupModel createPrivateGroup(@PathVariable("id") final long userId, CreatePrivateGroupRequest createPrivateGroupRequest){
        return groupService.createPrivateGroup(userId, createPrivateGroupRequest.getMaxSize());
    }

    @PatchMapping("private/{group}/add/{id}")
    @Operation(summary = " group")
    @ApiResponse(responseCode = "200", description = "Returns the created group")
    @ApiResponse(responseCode = "422", description = "")
    public void addUserToPrivateGroup(@PathVariable("group") final long groupId, @PathVariable("id") final long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(UserNotFoundException exception)
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
