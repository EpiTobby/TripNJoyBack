package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonSerialize
public class FirebaseTokenResponse {
    private final String token;

    @JsonProperty
    public String getToken()
    {
        return token;
    }
}
