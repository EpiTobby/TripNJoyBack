package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PostMessageRequest {

    private final long userId;
    private final String content;

    public PostMessageRequest(@JsonProperty("userId") final long userId,
                              @JsonProperty("content") final String content)
    {
        this.userId = userId;
        this.content = content;
    }
}
