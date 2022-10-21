package fr.tripnjoy.users.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "genders")
@Getter
@NoArgsConstructor
public class GenderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    public GenderEntity(String value)
    {
        id = null;
        this.value = value;
    }
}
