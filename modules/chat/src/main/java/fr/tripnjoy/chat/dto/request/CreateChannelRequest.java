package fr.tripnjoy.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CreateChannelRequest {
    private final String name;

    public CreateChannelRequest(@JsonProperty("name") final String name)
    {
        this.name = name;
    }
}
