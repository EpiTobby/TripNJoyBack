package fr.tobby.tripnjoyback.model.request.messaging;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UpdateSurveyRequest {
    private String question;
    private List<PossibleAnswerRequest> possibleAnswers;
}
