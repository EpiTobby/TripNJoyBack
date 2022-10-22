package fr.tripnjoy.users.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckJwtRequest {
    private final String jwt;

    public CheckJwtRequest(@JsonProperty("jwt") final String jwt)
    {
        this.jwt = jwt;
    }

    @JsonProperty("jwt")
    public String getJwt()
    {
        return jwt;
    }
}
