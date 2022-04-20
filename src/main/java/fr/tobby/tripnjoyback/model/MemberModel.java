package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class MemberModel {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private Instant birthDate;
    private Gender gender;
    private String profilePicture;
    private CityModel city;
    private Instant createdDate;
    private String phoneNumber;
    private boolean confirmed;

    public static MemberModel of(final UserEntity entity)
    {
        return new MemberModel(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getEmail(),
                entity.getBirthDate(),
                Gender.of(entity.getGender()),
                entity.getProfilePicture(),
                CityModel.of(entity.getCity()),
                entity.getCreatedDate(),
                entity.getPhoneNumber(),
                entity.isConfirmed()
        );
    }
}
