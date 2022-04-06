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

    @JoinColumn(name = "group_id")
    private long groupId;

    @JoinColumn(name = "user_id")
    private long userId;

    @JoinColumn(name = "profile_id")
    private long profileId;

    public GroupMemberEntity(long groupId, long userId, long profileId) {
        this.groupId = groupId;
        this.userId = userId;
        this.profileId = profileId;
    }
}
