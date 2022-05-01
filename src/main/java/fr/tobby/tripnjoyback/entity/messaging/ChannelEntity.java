package fr.tobby.tripnjoyback.entity.messaging;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "channels")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;
    @Setter
    private String name;
    @Setter
    private int index;

    public ChannelEntity(final GroupEntity group, final String name)
    {
        this.group = group;
        this.name = name;
    }
}
