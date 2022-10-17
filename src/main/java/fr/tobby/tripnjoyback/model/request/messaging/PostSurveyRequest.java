package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.model.MessageType;
import lombok.Getter;

import java.util.List;

@Getter
public class PostSurveyRequest extends PostMessageRequest{
    private List<PossibleAnswerRequest> possibleAnswers;
    private final boolean quizz;

    public PostSurveyRequest(@JsonProperty("userId") final long userId,
                             @JsonProperty("content") final String content,
                             @JsonProperty("quizz") final boolean quizz,
                             @JsonProperty("possibleAnswers") List<PossibleAnswerRequest> possibleAnswers) {
        super(userId, content, MessageType.SURVEY);
        this.possibleAnswers = possibleAnswers;
        this.quizz = quizz;
    }
}
