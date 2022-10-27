package fr.tripnjoy.groups.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonAutoDetect
public class JoinGroupWithoutInviteModel {
    private final String message;

    public JoinGroupWithoutInviteModel(@JsonProperty("message") final String message)
    {
        this.message = message;
    }
}
