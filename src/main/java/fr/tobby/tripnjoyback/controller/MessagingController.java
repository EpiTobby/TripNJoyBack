package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.model.websocket.Greeting;
import fr.tobby.tripnjoyback.model.websocket.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessagingController {

    @MessageMapping("/chat")
    @SendTo("/topic/response")
    public Greeting test(HelloMessage message)
    {
        System.out.println("Received message " + message.getName());
        return new Greeting(String.format("Hello %s!", message.getName()));
    }
}
