package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class JwtUserDetails {

    private final long userId;
    private final String username;
    private final List<String> roles;

    public JwtUserDetails(@JsonProperty("userId") final long userId, @JsonProperty("username") final String username,
                          @JsonProperty("roles") final List<String> roles)
    {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}
