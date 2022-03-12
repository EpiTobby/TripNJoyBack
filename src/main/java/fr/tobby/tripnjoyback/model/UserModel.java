package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class UserModel {

    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private Instant birthDate;
    private Gender gender;
    private String profilePicture;
    private CityModel city;
    private Instant createdDate;
    private String phoneNumber;

    public static UserModel of(final UserEntity entity)
    {
        return new UserModel(
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getBirthDate(),
                Gender.of(entity.getGender()),
                entity.getProfilePicture(),
                CityModel.of(entity.getCity()),
                entity.getCreatedDate(),
                entity.getPhoneNumber()
        );
    }
}