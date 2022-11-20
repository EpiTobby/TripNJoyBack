package fr.tripnjoy.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public abstract class AbstractNotificationRequest {

    private final String title;
    private final String body;
    private final Map<String, String> data;
    private final boolean saved;

    protected AbstractNotificationRequest(@JsonProperty("title") final String title, @JsonProperty("body") final String body,
                                          @JsonProperty("data") final Map<String, String> data, @JsonProperty("saved") final boolean saved)
    {
        this.title = title;
        this.body = body;
        this.data = data;
        this.saved = saved;
    }
}
