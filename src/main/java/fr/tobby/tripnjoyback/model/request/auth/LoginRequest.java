package fr.tobby.tripnjoyback.model.request.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonSerialize
@NoArgsConstructor
@Getter
@Setter
public final class LoginRequest {

    private String username;
    private String password;
}
