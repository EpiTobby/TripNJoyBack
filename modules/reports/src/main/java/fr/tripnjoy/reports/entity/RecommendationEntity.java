package fr.tripnjoy.reports.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommendations")
@Entity
public class RecommendationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reviewer_id")
    private long reviewer;

    @Column(name = "recommended_user_id")
    private long recommendedUser;

    @Setter
    private String comment;

    public RecommendationEntity(long submitter, long recommendedUser, String comment) {
        this.id = null;
        this.reviewer = submitter;
        this.recommendedUser = recommendedUser;
        this.comment = comment;
    }
}
