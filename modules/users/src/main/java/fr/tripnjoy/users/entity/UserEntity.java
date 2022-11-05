package fr.tripnjoy.users.entity;

import lombok.*;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "users")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    @Setter
    private String firstname;
    @Column(name = "last_name")
    @Setter
    private String lastname;
    @Setter
    private String password;
    @Setter
    private String email;
    @Column(name = "birthdate")
    @Setter
    private Instant birthDate;
    @ManyToOne()
    @JoinColumn(name = "gender_id")
    @Setter
    private GenderEntity gender;
    @Setter
    private String profilePicture;
    @ManyToOne
    @Setter
    @JoinColumn(name = "city_id")
    private CityEntity city;
    private Instant createdDate;
    @Setter
    private String phoneNumber;
    @Setter
    private boolean confirmed;
    @Setter
    @Nullable
    private String firebaseToken;
    @ManyToOne
    @Setter
    @JoinColumn(name = "language_id")
    private LanguageEntity language;
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<RoleEntity> roles;

    public UserEntity(String firstname, String lastname, String password, String email, Instant birthDate, GenderEntity gender, String profilePicture,
                      CityEntity city, Instant createdDate, String phoneNumber, LanguageEntity language)
    {
        this.id = null;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.profilePicture = profilePicture;
        this.city = city;
        this.createdDate = createdDate;
        this.phoneNumber = phoneNumber;
        this.language = language;
        this.confirmed = false;
    }
}
