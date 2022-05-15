package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "groups")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Setter
    @ManyToOne()
    @JoinColumn(name = "state_id")
    private StateEntity stateEntity;

    @ManyToOne()
    @JoinColumn(name = "owner_id")
    @Nullable
    private UserEntity owner;

    @Column(name = "max_size")
    private int maxSize;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "start_of_Trip")
    private Date startOfTrip;

    @Column(name = "end_of_Trip")
    private Date endOfTrip;

    @Column(name = "picture")
    private String picture;

    @OneToMany
    @JoinColumn(name = "group_id")
    public Collection<GroupMemberEntity> members;

    public long getNumberOfNonPendingUsers(){
        return members.stream().filter(m -> !m.isPending()).count();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "group_profiles", joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"))
    private ProfileEntity profile;

    @OneToMany
    @JoinColumn(name = "group_id")
    public Collection<ChannelEntity> channels;
}
