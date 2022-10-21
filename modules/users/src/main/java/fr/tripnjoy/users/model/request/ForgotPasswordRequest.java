package fr.tripnjoy.users.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.users.model.ModelWithEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class ForgotPasswordRequest extends ModelWithEmail {
}
