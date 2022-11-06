package fr.tripnjoy.reports.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.profiles.dto.response.RecommendationResponse;
import fr.tripnjoy.reports.entity.RecommendationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class RecommendationModel {
    private long id;
    private long reviewer;
    private long recommendedUser;
    private String comment;

    public static RecommendationModel of(RecommendationEntity recommendationEntity){
        return RecommendationModel.builder()
                .id(recommendationEntity.getId())
                .recommendedUser(recommendationEntity.getRecommendedUser())
                .reviewer(recommendationEntity.getReviewer())
                .comment(recommendationEntity.getComment())
                .build();
    }

    public RecommendationResponse toDtoResponse()
    {
        return new RecommendationResponse(id, reviewer, recommendedUser, comment);
    }
}
