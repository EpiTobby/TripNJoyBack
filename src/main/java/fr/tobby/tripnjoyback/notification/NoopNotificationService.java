package fr.tobby.tripnjoyback.notification;

import com.google.firebase.messaging.Message;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "tripnjoy.notification.enable", havingValue = "false")
public class NoopNotificationService implements INotificationService {

    @Override
    public @Nullable String send(final Message message)
    {
        return null; // Pass-through
    }
}
