package fr.tobby.tripnjoyback.model.request.questions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class MultipleAnswersModel implements AnswerModel {
    private List<String> answers;
}
