package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import lombok.*;

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
