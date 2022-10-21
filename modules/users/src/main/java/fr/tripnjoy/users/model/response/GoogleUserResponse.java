package fr.tripnjoy.users.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tripnjoy.users.model.UserModel;

@JsonSerialize
public record GoogleUserResponse(UserModel user, boolean newUser) {

}
