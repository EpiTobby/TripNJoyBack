package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.model.request.messaging.PostMessageRequest;
import fr.tobby.tripnjoyback.model.response.messaging.MessageResponse;
import fr.tobby.tripnjoyback.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
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

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    public MessagingController(final SimpMessagingTemplate simpMessagingTemplate, final MessageService messageService)
    {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat/{channelId}")
    public void test(@DestinationVariable("channelId") int channelId, final PostMessageRequest message)
    {
        MessageEntity createdMessage = messageService.postMessage(channelId, message);
        simpMessagingTemplate.convertAndSend("/topic/response/" + channelId, createdMessage);
    }

    @GetMapping("/chat/{channel_id}")
    @Operation(summary = "Get the most recent channel's messages, by pages of size 50")
    @ResponseBody
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    public List<MessageResponse> getMessages(@PathVariable("channel_id") long channelId,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return messageService.getChannelMessages(channelId, authentication.getName(), page)
                             .stream()
                             .map(MessageResponse::of)
                             .toList();
    }
}
