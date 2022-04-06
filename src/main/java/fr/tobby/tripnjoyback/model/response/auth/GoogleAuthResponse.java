package fr.tobby.tripnjoyback.model.response.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

@JsonSerialize
public record GoogleAuthResponse(String username, String token, boolean newUser) {

}
