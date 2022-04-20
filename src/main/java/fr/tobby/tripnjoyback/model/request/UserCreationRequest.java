package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public UserCreationRequest(final String email, final String firstname, final String lastname, final String password, final String gender,
                               final Date birthDate,
                               final String phoneNumber)
    {
        super(email);
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }
}
