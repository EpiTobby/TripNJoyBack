package fr.tobby.tripnjoyback.model.response.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record GoogleAuthResponse(String username, String token, boolean newUser) {

}
