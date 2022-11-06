package fr.tripnjoy.planning.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activities_members")
public class ActivityMemberEntity {

    @EmbeddedId
    private Ids ids;

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Ids implements Serializable {

        @ManyToOne
        private ActivityEntity activity;

        @Column(name = "participant_id")
        private long participantId;
    }
}
