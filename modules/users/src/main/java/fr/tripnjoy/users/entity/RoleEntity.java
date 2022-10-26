package fr.tripnjoy.users.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
public class RoleEntity {

    @Id
    private Long id;
    private String name;

    public String getAuthority()
    {
        return name;
    }

    public RoleEntity(String name)
    {
        this.name = name;
    }
}
