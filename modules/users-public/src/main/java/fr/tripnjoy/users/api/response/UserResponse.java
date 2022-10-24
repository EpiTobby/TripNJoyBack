package fr.tripnjoy.users.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.users.api.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@JsonAutoDetect
@Getter
public class UserResponse {

    private final long id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final Instant birthDate;
    private final Gender gender;
    private final String profilePicture;
    private final String city;
    private final Instant createdDate;
    private final String phoneNumber;
    private final boolean confirmed;
    private final String language;
}
