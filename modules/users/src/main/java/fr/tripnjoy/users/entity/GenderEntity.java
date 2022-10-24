package fr.tripnjoy.users.entity;

import fr.tripnjoy.users.api.model.Gender;
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

    public Gender toModel()
    {
        return switch (getValue())
                {
                    case "male" -> Gender.MALE;
                    case "female" -> Gender.FEMALE;
                    default -> Gender.NOT_SPECIFIED;
                };
    }
}
