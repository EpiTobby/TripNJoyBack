package fr.tripnjoy.chat.controller;

import fr.tripnjoy.chat.dto.request.PostMessageRequest;
import fr.tripnjoy.chat.entity.MessageEntity;
import fr.tripnjoy.chat.model.MessageResponse;
import fr.tripnjoy.chat.service.MessageService;
import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import io.swagger.v3.oas.annotations.Operation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    public List<MessageResponse> getMessages(@PathVariable("channel_id") long channelId,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                             @RequestHeader("username") String username)
    {
        return messageService.getChannelMessages(channelId, username, page)
                             .stream()
                             .map(MessageResponse::of)
                             .toList();
    }

    @GetMapping("/chat/{channel_id}/pinned")
    @Operation(summary = "Get all pinned messages")
    @ResponseBody
    public List<MessageResponse> getPinnedMessages(@PathVariable("channel_id") long channelId,
                                                   @RequestHeader("username") String username)
    {
        return messageService.getChannelPinnedMessages(channelId, username)
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
