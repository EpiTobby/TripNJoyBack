package fr.tobby.tripnjoyback.entity;

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

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private UserEntity reviewer;

    @ManyToOne
    @JoinColumn(name = "recommended_user_id")
    private UserEntity recommendedUser;

    @Setter
    private String comment;

    public RecommendationEntity(UserEntity submitter, UserEntity recommendedUser, String comment) {
        this.id = null;
        this.reviewer = submitter;
        this.recommendedUser = recommendedUser;
        this.comment = comment;
    }
}
