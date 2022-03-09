package fr.tobby.tripnjoyback.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "genders")
@Getter
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

    protected GenderEntity()
    {

    }
}
