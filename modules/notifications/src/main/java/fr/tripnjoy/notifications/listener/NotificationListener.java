package fr.tripnjoy.notifications.listener;

import fr.tripnjoy.notifications.dto.request.ToGroupNotificationRequest;
import fr.tripnjoy.notifications.dto.request.ToTokenNotificationRequest;
import fr.tripnjoy.notifications.dto.request.ToTopicNotificationRequest;
import fr.tripnjoy.notifications.dto.request.ToUserNotificationRequest;
import fr.tripnjoy.notifications.service.INotificationService;
import fr.tripnjoy.notifications.service.SavedNotificationService;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@RabbitListener(
        bindings = @QueueBinding(
                exchange = @Exchange(value = "amq.topic", type = "topic"),
                key = "notif",
                value = @Queue("notif")))
@Component
public class NotificationListener {

    private final INotificationService notificationService;
    private final SavedNotificationService savedNotificationService;

    public NotificationListener(final INotificationService notificationService,
                                final SavedNotificationService savedNotificationService)
    {
        this.notificationService = notificationService;
        this.savedNotificationService = savedNotificationService;
    }

    @RabbitHandler
    public void onToTopic(ToTopicNotificationRequest request)
    {
        if (request.isSaved())
            throw new UnsupportedOperationException();
        notificationService.sendToTopic(request.getTopic(), request.getTitle(), request.getBody(), request.getData());
    }

    @RabbitHandler
    public void onToToken(ToTokenNotificationRequest request)
    {
        if (request.isSaved())
            throw new UnsupportedOperationException();
        notificationService.sendToToken(request.getToken(), request.getTitle(), request.getBody(), request.getData());
    }

    @RabbitHandler
    public void onToGroup(ToGroupNotificationRequest request)
    {
        if (request.isSaved())
            savedNotificationService.sendToGroup(request.getGroupId(), request.getTitle(), request.getBody(), request.getData());
        else
            notificationService.sendToGroup(request.getGroupId(), request.getTitle(), request.getBody(), request.getData());
    }

    @RabbitHandler
    public void onToUser(ToUserNotificationRequest request)
    {
        if (request.isSaved())
            savedNotificationService.sendToUser(request.getUserId(), request.getTitle(), request.getBody(), request.getData());
        else
            throw new UnsupportedOperationException();
    }
}
