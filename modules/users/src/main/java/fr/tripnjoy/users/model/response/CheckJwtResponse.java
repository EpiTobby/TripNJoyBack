package fr.tripnjoy.users.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class CheckJwtResponse {

    private final boolean isValid;
    @Nullable
    private final JwtUserDetails userDetails;

    public CheckJwtResponse(@JsonProperty final boolean isValid, @JsonProperty final @Nullable JwtUserDetails userDetails)
    {
        this.isValid = isValid;
        this.userDetails = userDetails;
    }
}
