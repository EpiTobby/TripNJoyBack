package fr.tripnjoy.notifications.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonAutoDetect
public class ToTokenNotificationRequest extends AbstractNotificationRequest {
    private final String token;

    public ToTokenNotificationRequest(@JsonProperty("token") final String title, @JsonProperty("title") final String body,
                                      @JsonProperty("body") final Map<String, String> data, @JsonProperty("data") final String token, @JsonProperty("saved") final boolean saved)
    {
        super(title, body, data, saved);
        this.token = token;
    }
}
