package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.ModelWithEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
public class UserCreationRequest extends ModelWithEmail {

    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private String password;
    @NotNull
    private String gender;
    @NotNull
    private Date birthDate;
    @NotNull
    private String phoneNumber;

    public UserCreationRequest(final String email, final @NotNull String firstname, final @NotNull String lastname, final @NotNull String password,
                               final @NotNull String gender,
                               final @NotNull Date birthDate,
                               final @NotNull String phoneNumber)
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
