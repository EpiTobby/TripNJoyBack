package fr.tripnjoy.groups.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_groups")
@Entity
public class GroupMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "profile_id")
    @Nullable
    private Long profileId;

    private boolean pending;

    public GroupMemberEntity(GroupEntity groupEntity, long userId, @Nullable Long profileId, boolean pending)
    {
        this.group = groupEntity;
        this.userId = userId;
        this.profileId = profileId;
        this.pending = pending;
    }
}
