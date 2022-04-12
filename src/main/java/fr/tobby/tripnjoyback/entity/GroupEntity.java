package fr.tobby.tripnjoyback.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
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

    @Setter
    @ManyToOne()
    @JoinColumn(name = "state_id")
    private StateEntity stateEntity;

    @ManyToOne()
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Column(name = "max_size")
    private int maxSize;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "start_of_Trip")
    private Date startOfTrip;

    @Column(name = "end_of_Trip")
    private Date endOfTrip;

    @OneToMany
    @JoinColumn(name = "group_id")
    public Collection<GroupMemberEntity> members;
}
