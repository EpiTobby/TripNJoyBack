package fr.tripnjoy.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public class ToUserNotificationRequest extends AbstractNotificationRequest {
    private final long userId;

    public ToUserNotificationRequest(@JsonProperty("title") final String title, @JsonProperty("body") final String body,
                                     @JsonProperty("data") final Map<String, String> data, @JsonProperty("user") final long userId, @JsonProperty("saved") final boolean saved)
    {
        super(title, body, data, saved);
        this.userId = userId;
    }
}
