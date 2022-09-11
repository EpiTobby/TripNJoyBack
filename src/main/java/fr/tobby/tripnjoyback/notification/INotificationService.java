package fr.tobby.tripnjoyback.notification;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Sends notifications to a front-end client
 */
public interface INotificationService {

    /**
     * Send a message
     */
    @Nullable
    String send(Message message);

    /**
     * Send a message to a specific user
     */
    @Nullable
    default String sendToToken(String token, String title, String body, Map<String, String> data)
    {
        Notification notification = Notification.builder()
                                                .setTitle(title)
                                                .setBody(body)
                                                .build();
        Message message = Message.builder()
                                 .setToken(token)
                                 .setNotification(notification)
                                 .putAllData(data)
                                 .build();
        return send(message);
    }

    /**
     * Send a message to a specific topic
     */
    @Nullable
    default String sendToTopic(String topic, String title, String body, Map<String, String> data)
    {
        Notification notification = Notification.builder()
                                                .setTitle(title)
                                                .setBody(body)
                                                .build();
        Message message = Message.builder()
                                 .setTopic(topic)
                                 .setNotification(notification)
                                 .putAllData(data)
                                 .build();
        return send(message);
    }
}
