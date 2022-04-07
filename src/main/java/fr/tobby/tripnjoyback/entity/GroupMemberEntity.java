package fr.tobby.tripnjoyback.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_groups")
@Entity
public class GroupMemberEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    public GroupMemberEntity(GroupEntity groupEntity, UserEntity userEntity, ProfileEntity profileEntity) {
        this.group = groupEntity;
        this.user = userEntity;
        this.profile = profileEntity;
    }
}
