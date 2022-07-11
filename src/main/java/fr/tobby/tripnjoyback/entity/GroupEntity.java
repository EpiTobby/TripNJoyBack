package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "groups")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "state_id")
    private StateEntity stateEntity;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "owner_id")
    @Nullable
    private UserEntity owner;

    @Setter
    @Column(name = "max_size")
    private int maxSize;

    @Column(name = "created_date")
    private Date createdDate;

    @Setter
    @Column(name = "start_of_Trip")
    private Date startOfTrip;

    @Setter
    @Column(name = "end_of_Trip")
    private Date endOfTrip;

    @Setter
    @Column(name = "picture")
    private String picture;

    @Setter
    @Column(name = "destination")
    private String destination;

    @Setter
    @OneToMany
    @JoinColumn(name = "group_id")
    public Collection<GroupMemberEntity> members;

    public long getNumberOfNonPendingUsers(){
        return members.stream().filter(m -> !m.isPending()).count();
    }

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "group_profiles", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"))
    private ProfileEntity profile;

    @Setter
    @OneToMany
    @JoinColumn(name = "group_id")
    public Collection<ChannelEntity> channels;

    public boolean isMember(long userId)
    {
        return getMembers().stream()
                           .anyMatch(groupMember -> groupMember.getUser().getId().equals(userId));
    }

    public Optional<GroupMemberEntity> findMember(long userId)
    {
        return getMembers().stream()
                           .filter(groupMember -> groupMember.getUser().getId().equals(userId))
                           .findAny();
    }
}
