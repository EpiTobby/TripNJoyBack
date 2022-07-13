package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.DeleteChannelException;
import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.ChannelModel;
import fr.tobby.tripnjoyback.model.request.CreateChannelRequest;
import fr.tobby.tripnjoyback.model.request.UpdateChannelRequest;
import fr.tobby.tripnjoyback.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "channels")
public class ChannelController {
    private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    public static final String ERROR_ON_REQUEST = "Error on request";
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping("{group}")
    @Operation(summary = "Get all the channels from a group")
    @ApiResponse(responseCode = "200", description = "Return the list of channels of a group")
    @ApiResponse(responseCode = "422", description = "The group does not exist")
    public Collection<ChannelModel> getGroupChannels(@PathVariable("group") long groupId){
        channelService.checkMember(groupId);
        return channelService.getGroupChannels(groupId);
    }

    @PostMapping("{group}")
    @Operation(summary = "Create a channel")
    @ApiResponse(responseCode = "201", description = "Return the created channel")
    @ApiResponse(responseCode = "422", description = "The group id does not correspond to an existing group")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelModel createChannel(@PathVariable("group") long groupId, @RequestBody CreateChannelRequest createChannelRequest){
        channelService.checkMember(groupId);
        return channelService.createChannel(groupId, createChannelRequest);
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a channel")
    @ApiResponse(responseCode = "200", description = "The channel has been updated")
    @ApiResponse(responseCode = "403", description = "The client  don't have access to this channel")
    @ApiResponse(responseCode = "422", description = "The channel does not exist")
    public void updateChannel(@PathVariable("id") long channelId, @RequestBody  UpdateChannelRequest updateChannelRequest){
        channelService.checkUserHasAccessToChannel(channelId);
        channelService.updateChannel(channelId, updateChannelRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a channel")
    @ApiResponse(responseCode = "200", description = "The channel has been deleted")
    @ApiResponse(responseCode = "422", description = "The channel does not exist")
    public void deleteChannel(@PathVariable("id") long channelId){
        channelService.checkUserIsOwnerOfGroup(channelId);
        channelService.deleteChannel(channelId);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(EntityNotFoundException exception)
    {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(DeleteChannelException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(DeleteChannelException exception) {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }
}
