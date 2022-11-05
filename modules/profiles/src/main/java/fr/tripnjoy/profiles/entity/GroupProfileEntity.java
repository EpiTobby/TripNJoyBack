package fr.tripnjoy.profiles.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "group_profiles")
@NoArgsConstructor
@AllArgsConstructor
public class GroupProfileEntity {

    @EmbeddedId
    private Ids ids;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ids implements Serializable {

        @Column(name = "group_id")
        private long groupId;

        @ManyToOne
        private ProfileEntity profile;
    }
}
