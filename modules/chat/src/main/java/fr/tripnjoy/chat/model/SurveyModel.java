package fr.tripnjoy.chat.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.chat.entity.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private long submitter;
    private boolean quizz;
    private Date sendDate;
    private Date modifiedDate;
    private boolean isMultipleChoiceSurvey;
    private List<PossibleAnswerModel> possibleAnswers;
    private List<VoteModel> votes;
    private MessageType type;

    public static SurveyModel of(SurveyEntity surveyEntity) {
        return SurveyModel.builder()
                .id(surveyEntity.getId())
                .channelId(surveyEntity.getChannel().getId())
                .question(surveyEntity.getQuestion())
                .sendDate(surveyEntity.getSendDate())
                .submitter(surveyEntity.getSubmitter())
                .modifiedDate(surveyEntity.getModifiedDate())
                .isMultipleChoiceSurvey(surveyEntity.isMultipleChoiceSurvey())
                .possibleAnswers(surveyEntity.getAnswers().stream().map(PossibleAnswerModel::of).toList())
                .votes(surveyEntity.getVotes().stream().map(VoteModel::of).toList())
                .type(MessageType.SURVEY).build();
    }
}
