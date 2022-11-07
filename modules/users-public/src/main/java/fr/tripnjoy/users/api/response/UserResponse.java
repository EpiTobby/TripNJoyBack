package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.users.api.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonAutoDetect
@Getter
public class UserResponse {

    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private Instant birthDate;
    private Gender gender;
    private String profilePicture;
    private String city;
    private Instant createdDate;
    private String phoneNumber;
    private boolean confirmed;
    private String language;
}
