package fr.tobby.tripnjoyback.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name  = "users")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstname;
    @Column(name = "last_name")
    private String lastname;
    private String password;
    private String email;
    @Column(name = "birthdate")
    private Instant birthDate;
    @ManyToOne()
    @JoinColumn(name = "gender_id")
    private GenderEntity gender;
    private String profilePicture;
    @ManyToOne
    @Setter
    @JoinColumn(name = "city_id")
    private CityEntity city;
    private Instant createdDate;
    @Setter
    private String phoneNumber;

    public UserEntity(String firstname, String lastname, String password, String email, Instant birthDate, GenderEntity gender, String profilePicture, CityEntity city, Instant createdDate, String phoneNumber) {
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
    }
}
