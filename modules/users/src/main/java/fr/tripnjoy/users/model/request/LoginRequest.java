package fr.tripnjoy.users.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonSerialize
@NoArgsConstructor
@Getter
@Setter
public final class LoginRequest {

    @Schema(description = "Aka email")
    private String username;
    private String password;

    @JsonProperty("username")
    public void setUsername(String username){
        this.username = username.toLowerCase().trim();
    }
}
