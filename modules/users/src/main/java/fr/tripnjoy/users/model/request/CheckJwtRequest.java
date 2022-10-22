package fr.tripnjoy.users.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CheckJwtRequest {
    private final String jwt;

    public CheckJwtRequest(@JsonProperty final String jwt)
    {
        this.jwt = jwt;
    }
}
