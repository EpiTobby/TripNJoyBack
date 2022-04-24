package fr.tobby.tripnjoyback.entity.messaging;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "channels")
@Getter
public class ChannelEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    private String name;
    private int index;

    public ChannelEntity()
    {
    }

    public ChannelEntity(final GroupEntity group, final String name)
    {
        this.group = group;
        this.name = name;
    }
}
