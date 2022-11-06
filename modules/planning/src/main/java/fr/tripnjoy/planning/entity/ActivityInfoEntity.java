package fr.tripnjoy.planning.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "activities_info")
@NoArgsConstructor
@Getter
public class ActivityInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    public ActivityInfoEntity(final String content)
    {
        this.content = content;
    }
}
