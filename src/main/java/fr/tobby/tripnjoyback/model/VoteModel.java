package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.VoteEntity;
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
public class VoteModel {
    private long id;
    private GroupMemberModel voter;
    private PossibleAnswerModel answer;

    public static VoteModel of(VoteEntity voteEntity){
        return VoteModel.builder()
                .id(voteEntity.getId())
                .answer(PossibleAnswerModel.of(voteEntity.getAnswer()))
                .voter(GroupMemberModel.of(voteEntity.getVoter())).build();
    }
}
