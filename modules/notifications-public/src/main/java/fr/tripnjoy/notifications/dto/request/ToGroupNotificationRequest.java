package fr.tripnjoy.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public class ToGroupNotificationRequest extends AbstractNotificationRequest {
    private final long groupId;

    public ToGroupNotificationRequest(@JsonProperty("title") final String title, @JsonProperty("body") final String body,
                                      @JsonProperty("data") final Map<String, String> data, @JsonProperty("group") final long groupId, @JsonProperty("saved") final boolean saved)
    {
        super(title, body, data, saved);
        this.groupId = groupId;
    }
}
