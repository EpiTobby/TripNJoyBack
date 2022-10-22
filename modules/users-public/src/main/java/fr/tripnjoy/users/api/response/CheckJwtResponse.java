package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class CheckJwtResponse {

    private final boolean isValid;
    @Nullable
    private final JwtUserDetails userDetails;

    public CheckJwtResponse(@JsonProperty("isValid") final boolean isValid, @JsonProperty("userDetails") final @Nullable JwtUserDetails userDetails)
    {
        this.isValid = isValid;
        this.userDetails = userDetails;
    }

    @JsonProperty("isValid")
    public boolean isValid()
    {
        return isValid;
    }

    @JsonProperty("userDetails")
    public JwtUserDetails getUserDetails()
    {
        return userDetails;
    }
}
