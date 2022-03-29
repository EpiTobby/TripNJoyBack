package fr.tobby.tripnjoyback.model.response.auth;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize
@JsonAutoDetect
public class AuthTokenResponse {

    @Getter
    private final String token;

    public AuthTokenResponse(final String token)
    {
        this.token = token;
    }
}
