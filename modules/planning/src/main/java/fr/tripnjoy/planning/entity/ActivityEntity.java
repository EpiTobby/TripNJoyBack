package fr.tripnjoy.planning.entity;

import fr.tripnjoy.planning.dto.response.ActivityResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

    @Column(name = "group_id")
    private long groupId;

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

    @OneToMany
    @JoinColumn(name = "activity_id")
    private Collection<ActivityMemberEntity> participants = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "activity_id")
    private List<ActivityInfoEntity> infos = new ArrayList<>();

    public ActivityEntity(final long groupId, final String name, final String description, final Date startDate, final Date endDate,
                          final String color, final String location, final String icon)
    {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.color = color;
        this.location = location;
        this.icon = icon;
        participants = new ArrayList<>();
    }

    @NotNull
    public ActivityResponse toDtoResponse()
    {
        return new ActivityResponse(this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStartDate(),
                this.getEndDate(),
                this.getParticipants().stream().map(member -> member.getIds().getParticipantId()).toList(),
                this.getColor(),
                this.getLocation(),
                this.getIcon(),
                this.getInfos().stream().map(ActivityInfoEntity::getContent).toList());
    }
}
