package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.model.MessageType;
import lombok.Getter;

@Getter
public class PostMessageRequest {

    private final long userId;
    private final String content;
    private final MessageType type;

    public PostMessageRequest(@JsonProperty("userId") final long userId,
                              @JsonProperty("content") final String content,
                              @JsonProperty("type") final MessageType type)
    {
        this.userId = userId;
        this.content = content;
        this.type = type;
    }
}
