package fr.tobby.tripnjoyback.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name  = "profiles")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity {
    @Id
    private long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Column(name = "active")
    @Setter
    private boolean active;
}
