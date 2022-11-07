package fr.tripnjoy.chat.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tripnjoy.chat.entity.SurveyAnswerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class PossibleAnswerModel {
    private long id;
    private String content;
    private boolean rightAnswer;

    public static PossibleAnswerModel of(SurveyAnswerEntity answerEntity){
        return PossibleAnswerModel.builder()
                .id(answerEntity.getId())
                .content(answerEntity.getContent())
                .rightAnswer(answerEntity.isRightAnswer())
                .build();
    }
}
