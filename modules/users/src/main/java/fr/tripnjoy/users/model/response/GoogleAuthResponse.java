package fr.tripnjoy.users.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record GoogleAuthResponse(String username, String token, boolean newUser) {

}
