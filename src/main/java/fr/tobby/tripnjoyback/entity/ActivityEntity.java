package fr.tobby.tripnjoyback.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "activities")
@Getter
@NoArgsConstructor
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private Date startDate;
    @Setter
    private Date endDate;
    @Setter
    private String color;
    @Setter
    private String location;
    @Setter
    private String icon;

    @ManyToMany
    @JoinTable(name = "activities_members",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private Collection<GroupMemberEntity> participants = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "activity_id")
    private List<ActivityInfoEntity> infos = new ArrayList<>();

    public ActivityEntity(final GroupEntity group, final String name, final String description, final Date startDate, final Date endDate,
                          final String color, final String location, final String icon)
    {
        this.group = group;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.location = location;
        this.icon = icon;
        participants = new ArrayList<>();
    }
}
