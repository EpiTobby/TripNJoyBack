package fr.tobby.tripnjoyback.model.request.questions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class AvailabilityAnswerModel implements AnswerModel {
    private Date startDate;
    private Date endDate;
}
