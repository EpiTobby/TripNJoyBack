package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
    private GroupMemberModel voter;
    private PossibleAnswerModel answer;
}
