package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@JsonSerialize
@JsonAutoDetect
@Setter
@NoArgsConstructor
public class FirebaseTokenResponse {
    private String token;

    @JsonProperty
    @Nullable
    public String getToken()
    {
        return token;
    }
}
