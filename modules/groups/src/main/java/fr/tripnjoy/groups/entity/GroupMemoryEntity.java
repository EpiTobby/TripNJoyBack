package fr.tripnjoy.groups.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "group_memories")
@Entity
public class GroupMemoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Setter
    @Column(name = "memory_url")
    private String memoryUrl;

    public GroupMemoryEntity(GroupEntity group, String memoryUrl) {
        this.group = group;
        this.memoryUrl = memoryUrl;
    }
}
