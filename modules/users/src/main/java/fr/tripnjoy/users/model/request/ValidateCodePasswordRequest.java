package fr.tripnjoy.users.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.users.model.ModelWithEmail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class ValidateCodePasswordRequest extends ModelWithEmail {
    private String value;
    private String newPassword;
}
