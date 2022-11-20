package fr.tripnjoy.notifications.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.notifications.entity.NotificationEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotificationModel {
    private final String title;
    private final String body;
    private final long userId;
    private final long id;
    private final String firebaseId;

    @JsonProperty
    public String getTitle()
    {
        return title;
    }

    @JsonProperty
    public String getBody()
    {
        return body;
    }

    @JsonProperty
    public long getUserId()
    {
        return userId;
    }

    @JsonProperty
    public long getId()
    {
        return id;
    }

    @JsonProperty
    public String getFirebaseId()
    {
        return firebaseId;
    }

    public static NotificationModel from(NotificationEntity entity)
    {
        return new NotificationModel(
                entity.getTitle(),
                entity.getBody(),
                entity.getUserId(),
                entity.getId(),
                entity.getFirebaseId()
        );
    }
}
