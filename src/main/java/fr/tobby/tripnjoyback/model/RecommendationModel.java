package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.RecommendationEntity;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
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
    private GroupMemberModel reviewer;
    private GroupMemberModel recommendedUser;
    private String comment;

    public static RecommendationModel of(RecommendationEntity recommendationEntity){
        return RecommendationModel.builder()
                .id(recommendationEntity.getId())
                .recommendedUser(GroupMemberModel.of(recommendationEntity.getRecommendedUser()))
                .reviewer(GroupMemberModel.of(recommendationEntity.getReviewer()))
                .comment(recommendationEntity.getComment())
                .build();
    }
}
