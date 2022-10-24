package fr.tripnjoy.groups.entity;

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
    @Column(name = "owner_id")
    @Nullable
    private Long owner;

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

    public boolean isMember(long userId)
    {
        return getMembers().stream()
                           .anyMatch(groupMember -> groupMember.getUserId() == userId);
    }

    public long getNumberOfNonPendingUsers()
    {
        return members.stream().filter(m -> !m.isPending()).count();
    }

    public Optional<GroupMemberEntity> findMember(long userId)
    {
        return getMembers().stream()
                           .filter(groupMember -> groupMember.getUserId() == userId)
                           .findAny();
    }
}
