package fr.tobby.tripnjoyback.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "tripnjoy.notification.enable", havingValue = "true")
public class NotificationService implements INotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final FirebaseMessaging messaging;

    public NotificationService(final FirebaseMessaging messaging)
    {
        this.messaging = messaging;
    }

    @Override
    @Nullable
    public String send(final Message message)
    {
        try
        {
            return messaging.send(message);
        }
        catch (FirebaseMessagingException e)
        {
            logger.error("Failed to send notification", e);
            return null;
        }
    }
}
