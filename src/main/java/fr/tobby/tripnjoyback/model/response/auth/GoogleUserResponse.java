package fr.tobby.tripnjoyback.model.response.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tobby.tripnjoyback.model.UserModel;

@JsonSerialize
public record GoogleUserResponse(UserModel user, boolean newUser) {

}
