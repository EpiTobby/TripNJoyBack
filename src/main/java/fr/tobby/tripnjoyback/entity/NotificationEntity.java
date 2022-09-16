package fr.tobby.tripnjoyback.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    @ManyToOne
    private UserEntity user;

    public NotificationEntity(final String title, final String body, final UserEntity user)
    {
        this.title = title;
        this.body = body;
        this.user = user;
    }
}
