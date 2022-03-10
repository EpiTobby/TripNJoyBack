package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.CityEntity;
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
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getBirthdate(),
                Gender.of(entity.getGender()),
                entity.getProfile_picture(),
                CityModel.of(entity.getCity()),
                entity.getCreatedDate(),
                entity.getPhoneNumber()
        );
    }
}
