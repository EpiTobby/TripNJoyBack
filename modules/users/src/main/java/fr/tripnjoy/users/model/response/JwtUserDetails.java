package fr.tripnjoy.users.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class JwtUserDetails {

    private final long userId;
    private final String username;
    private final List<String> roles;

    public JwtUserDetails(@JsonProperty final long userId, @JsonProperty final String username, @JsonProperty final List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public static JwtUserDetails fromUserDetails(final long userId, final UserDetails userDetails)
    {
        return new JwtUserDetails(
                userId,
                userDetails.getUsername(),
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }
}
