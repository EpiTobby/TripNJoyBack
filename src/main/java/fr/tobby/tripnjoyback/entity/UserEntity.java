package fr.tobby.tripnjoyback.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name  = "users")
@Builder
@Getter
@NoArgsConstructor
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
    @JoinColumn(name = "city_id")
    private CityEntity city;
    private Instant createdDate;

    public UserEntity(Long id, String firstName, String lastName, String password, String email, Instant birthdate, GenderEntity gender, String profile_picture, CityEntity city, Instant createdDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.profile_picture = profile_picture;
        this.city = city;
        this.createdDate = createdDate;
    }
}
