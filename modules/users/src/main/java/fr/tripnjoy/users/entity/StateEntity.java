package fr.tripnjoy.users.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "states", uniqueConstraints = @UniqueConstraint(name = "states_value_uindex", columnNames = "value"))
@Getter
@NoArgsConstructor
public class StateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;

    public StateEntity(String value)
    {
        id = null;
        this.value = value;
    }
}
