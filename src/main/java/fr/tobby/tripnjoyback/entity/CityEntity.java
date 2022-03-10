package fr.tobby.tripnjoyback.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cities")
@NoArgsConstructor
@Getter
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public CityEntity(String name) {
        this.id = null;
        this.name = name;
    }
}
