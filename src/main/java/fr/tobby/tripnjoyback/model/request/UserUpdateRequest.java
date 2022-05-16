package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.CityModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UserUpdateRequest {
    private String firstname;
    private String lastname;
    private String profilePicture;
    private CityModel city;
    private String phoneNumber;
    private Date birthdate;
    private String gender;
    private String language;
}
