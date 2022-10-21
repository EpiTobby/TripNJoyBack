package fr.tripnjoy.users.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
public class RoleEntity implements GrantedAuthority {

    @Id
    private Long id;
    private String name;

    @Override
    public String getAuthority()
    {
        return name;
    }

    public RoleEntity(String name)
    {
        this.name = name;
    }
}
