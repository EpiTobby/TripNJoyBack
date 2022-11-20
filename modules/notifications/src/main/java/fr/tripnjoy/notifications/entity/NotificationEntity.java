package fr.tripnjoy.notifications.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    private String firebaseId;
    @Column(name = "user_id")
    private long userId;

    public NotificationEntity(final String title, final String body, final long userId, final String firebaseId)
    {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.firebaseId = firebaseId;
    }
}
