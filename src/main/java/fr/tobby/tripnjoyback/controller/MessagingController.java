package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.request.messaging.PostMessageRequest;
import fr.tobby.tripnjoyback.model.response.messaging.MessageResponse;
import fr.tobby.tripnjoyback.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessagingController {
    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    public MessagingController(final SimpMessagingTemplate simpMessagingTemplate, final MessageService messageService)
    {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat/{channelId}")
    public void sendMessage(@DestinationVariable("channelId") int channelId, final PostMessageRequest message)
    {
        MessageEntity createdMessage = messageService.postMessage(channelId, message);
        simpMessagingTemplate.convertAndSend("/topic/response/" + channelId, MessageResponse.of(createdMessage));
    }

    @GetMapping("/chat/{channel_id}")
    @Operation(summary = "Get the most recent channel's messages, by pages of size 50")
    @ResponseBody
    public List<MessageResponse> getMessages(@PathVariable("channel_id") long channelId,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return messageService.getChannelMessages(channelId, authentication.getName(), page)
                             .stream()
                             .map(MessageResponse::of)
                             .toList();
    }

    @GetMapping("/chat/{channel_id}/pinned")
    @Operation(summary = "Get all pinned messages")
    @ResponseBody
    public List<MessageResponse> getPinnedMessages(@PathVariable("channel_id") long channelId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return messageService.getChannelPinnedMessages(channelId, authentication.getName())
                             .stream()
                             .map(MessageResponse::of)
                             .toList();
    }

    @PatchMapping("/chat/{message_id}/pinned")
    @Operation(summary = "Pin a message")
    @ResponseBody
    public MessageResponse pinMessage(@PathVariable("message_id") long messageId,
                                      @RequestParam(value = "pin", required = false, defaultValue = "true") final boolean pin)
    {
        return MessageResponse.of(messageService.pinMessage(messageId, pin));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String onEntityNotFound(@NotNull final EntityNotFoundException exception)
    {
        logger.debug(exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String onEntityNotFound(@NotNull final ForbiddenOperationException exception)
    {
        logger.debug(exception.getMessage());
        return "You are not authorized to perform this operation.";
    }
}
