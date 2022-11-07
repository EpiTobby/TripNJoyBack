package fr.tripnjoy.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.chat.model.MessageType;
import lombok.Getter;

@Getter
public class PostMessageRequest {

    protected final long userId;
    protected final String content;
    protected final MessageType type;

    public PostMessageRequest(@JsonProperty("userId") final long userId,
                              @JsonProperty("content") final String content,
                              @JsonProperty("type") final MessageType type)
    {
        this.userId = userId;
        this.content = content;
        this.type = type;
    }
}
