package fr.tobby.tripnjoyback.model.request.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonSerialize
@NoArgsConstructor
@Getter
@Setter
public final class GoogleRequest extends ModelWithEmail {

    private String firstname;
    private String lastname;
    private String accessToken;
    private String profilePicture;
    private String phoneNumber;
}
