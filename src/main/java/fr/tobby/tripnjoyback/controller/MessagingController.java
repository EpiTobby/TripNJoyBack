package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.model.request.messaging.PostMessageRequest;
import fr.tobby.tripnjoyback.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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
}
