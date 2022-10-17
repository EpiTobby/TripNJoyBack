package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class SurveyModel {
    private long id;
    private long channelId;
    private String question;
    private boolean quizz;
    private Date sendDate;
    private Date modifiedDate;
    private List<PossibleAnswerModel> possibleAnswers;
    private List<VoteModel> votes;

    public static SurveyModel of(SurveyEntity surveyEntity){
        return SurveyModel.builder()
                .id(surveyEntity.getId())
                .channelId(surveyEntity.getChannel().getId())
                .question(surveyEntity.getQuestion())
                .sendDate(surveyEntity.getSendDate())
                .modifiedDate(surveyEntity.getModifiedDate())
                .possibleAnswers(surveyEntity.getAnswers().stream().map(PossibleAnswerModel::of).toList())
                .votes(surveyEntity.getVotes().stream().map(VoteModel::of).toList()).build();
    }
}
