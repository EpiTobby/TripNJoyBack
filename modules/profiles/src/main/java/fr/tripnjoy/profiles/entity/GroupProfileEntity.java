package fr.tripnjoy.profiles.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "group_profiles")
public class GroupProfileEntity {

    @EmbeddedId
    private Ids ids;

    @Embeddable
    public static class Ids implements Serializable {

        @Column(name = "group_id")
        private long groupId;

        @ManyToOne
        private ProfileEntity profile;
    }
}
