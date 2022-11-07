package fr.tripnjoy.chat.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.chat.entity.VoteEntity;
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
    private long voter;
    private PossibleAnswerModel answer;

    public static VoteModel of(VoteEntity voteEntity){
        return VoteModel.builder()
                        .id(voteEntity.getId())
                        .answer(PossibleAnswerModel.of(voteEntity.getAnswer()))
                        .voter(voteEntity.getVoter())
                        .build();
    }
}
