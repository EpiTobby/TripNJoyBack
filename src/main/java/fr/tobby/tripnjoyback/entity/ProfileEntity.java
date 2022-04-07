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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    private String name;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "active")
    @Setter
    private boolean active;
}
