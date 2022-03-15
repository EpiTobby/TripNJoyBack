package fr.tobby.tripnjoyback.model.response.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize
@Getter
public final class LoginResponse {

    public static final LoginResponse FAILED = new LoginResponse("", "", false);

    private final String username;
    private final String token;
    private final boolean authenticated;

    public LoginResponse(final String username, final String token, final boolean authenticated)
    {
        this.username = username;
        this.token = token;
        this.authenticated = authenticated;
    }
}
