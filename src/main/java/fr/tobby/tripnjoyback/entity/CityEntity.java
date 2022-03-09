package fr.tobby.tripnjoyback.entity;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public CityEntity(String name) {
        this.id = null;
        this.name = name;
    }

    public CityEntity(){

    }
}
