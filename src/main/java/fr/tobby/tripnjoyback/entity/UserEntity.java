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
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Instant birthdate;
    @ManyToOne()
    @JoinColumn(name = "gender_id")
    private GenderEntity gender;
    private String profile_picture;
    @ManyToOne
    @Setter
    @JoinColumn(name = "city_id")
    private CityEntity city;
    private Instant createdDate;
    @Setter
    private String phoneNumber;

    public UserEntity(String firstName, String lastName, String password, String email, Instant birthdate, GenderEntity gender, String profile_picture, CityEntity city, Instant createdDate, String phoneNumber) {
        this.id = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.profile_picture = profile_picture;
        this.city = city;
        this.createdDate = createdDate;
        this.phoneNumber = phoneNumber;
    }
}