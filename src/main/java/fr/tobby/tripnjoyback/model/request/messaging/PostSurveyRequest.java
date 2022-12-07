package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.model.MessageType;
import lombok.Getter;

import java.util.List;

@Getter
public class PostSurveyRequest extends PostMessageRequest{
    private final List<PossibleAnswerRequest> possibleAnswers;
    private final boolean quizz;
    private final boolean isMultipleChoiceSurvey;

    public PostSurveyRequest(@JsonProperty("userId") final long userId,
                             @JsonProperty("content") final String content,
                             @JsonProperty("quizz") final boolean quizz,
                             @JsonProperty("possibleAnswers") List<PossibleAnswerRequest> possibleAnswers,
                             @JsonProperty("isMultipleChoiceSurvey") boolean isMultipleChoiceSurvey) {
        super(userId, content, MessageType.SURVEY);
        this.possibleAnswers = possibleAnswers;
        this.quizz = quizz;
        this.isMultipleChoiceSurvey = isMultipleChoiceSurvey;
    }
}
