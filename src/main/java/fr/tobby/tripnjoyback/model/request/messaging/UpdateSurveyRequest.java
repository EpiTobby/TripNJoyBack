package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UpdateSurveyRequest {
    @Nullable
    private String question;
    private List<PossibleAnswerRequest> possibleAnswers;
    private boolean multipleChoiceSurvey;
}
