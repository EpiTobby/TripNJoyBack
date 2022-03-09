package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonAutoDetect
@NoArgsConstructor
public class UserCreationModel {

    private String firstname;
    private String lastname;
    private String password;
    private String gender;
    private String email;
    private Instant birthDate;
    private String phoneNumber;

}
