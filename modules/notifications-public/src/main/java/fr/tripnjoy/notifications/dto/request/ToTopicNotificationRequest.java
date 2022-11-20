package fr.tripnjoy.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public class ToTopicNotificationRequest extends AbstractNotificationRequest {
    private final String topic;

    public ToTopicNotificationRequest(@JsonProperty("title") final String title, @JsonProperty("body") final String body,
                                      @JsonProperty("data") final Map<String, String> data, @JsonProperty("topic") final String topic, @JsonProperty("saved") final boolean saved)
    {
        super(title, body, data, saved);
        this.topic = topic;
    }
}
