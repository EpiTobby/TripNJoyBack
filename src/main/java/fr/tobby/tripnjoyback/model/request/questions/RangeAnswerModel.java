package fr.tobby.tripnjoyback.model.request.questions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class RangeAnswerModel implements AnswerModel {
    private int minValue;
    private int maxValue;
}
