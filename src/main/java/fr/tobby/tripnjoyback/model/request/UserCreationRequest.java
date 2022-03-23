package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
public class UserCreationRequest extends ModelWithEmail {

    private String firstname;
    private String lastname;
    private String password;
    private String gender;
    private Date birthDate;
    private String phoneNumber;

}
