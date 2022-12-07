package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Collection;

@AllArgsConstructor
@Getter
public class UserModel {

    private long id;
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
    private boolean confirmed;
    private String language;
    private Collection<UserRole> roles;

    public static UserModel of(final UserEntity entity)
    {
        Collection<UserRole> roles = entity.getRoles().stream()
                                           .map(role -> UserRole.of(role.getName()))
                                           .toList();
        return new UserModel(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPassword(),
                entity.getEmail(),
                entity.getBirthDate(),
                Gender.of(entity.getGender()),
                entity.getProfilePicture(),
                CityModel.of(entity.getCity()),
                entity.getCreatedDate(),
                entity.getPhoneNumber(),
                entity.isConfirmed(),
                entity.getLanguage().getValue(),
                roles
        );
    }

    @Override
    public String toString()
    {
        return "UserModel{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                ", gender=" + gender +
                ", profilePicture='" + profilePicture + '\'' +
                ", city=" + city +
                ", createdDate=" + createdDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", confirmed=" + confirmed +
                ", roles=" + roles +
                '}';
    }
}
