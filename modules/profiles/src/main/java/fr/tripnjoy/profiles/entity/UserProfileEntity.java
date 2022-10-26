package fr.tripnjoy.profiles.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_profiles")
@NoArgsConstructor
public class UserProfileEntity {

    @EmbeddedId
    private Ids ids;

    public UserProfileEntity(long userId, ProfileEntity profileEntity)
    {
        ids = new Ids(userId, profileEntity);
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ids implements Serializable {

        @Column(name = "user_id")
        private long userId;

        @ManyToOne(cascade = CascadeType.ALL)
        private ProfileEntity profile;
    }
}
