package fr.tripnjoy.users.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize
@Getter
public final class LoginResponse {

    private final String username;
    private final String token;

    public LoginResponse(final String username, final String token)
    {
        this.username = username;
        this.token = token;
    }
}
