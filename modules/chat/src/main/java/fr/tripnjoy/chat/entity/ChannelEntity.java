package fr.tripnjoy.chat.entity;

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

    @Column(name = "group_id")
    private long group;
    @Setter
    private String name;
    @Setter
    private int index;

    public ChannelEntity(final long group, final String name)
    {
        this.group = group;
        this.name = name;
    }
}
