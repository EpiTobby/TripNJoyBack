package fr.tobby.tripnjoyback.model.request.profiles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.request.questions.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.questions.MultipleAnswersModel;
import fr.tobby.tripnjoyback.model.request.questions.RangeAnswerModel;
import fr.tobby.tripnjoyback.model.request.questions.SingleAnswerModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class AddProfileModel {
    private AvailabilityAnswerModel availability;
    private RangeAnswerModel duration;
    private RangeAnswerModel budget;
    private MultipleAnswersModel destinationTypes;
    private RangeAnswerModel ages;
    private SingleAnswerModel travelWithPersonFromSameCity;
    private SingleAnswerModel travelWithPersonFromSameCountry;
    private SingleAnswerModel travelWithPersonSameLanguage;
    private SingleAnswerModel gender;
    private RangeAnswerModel groupeSize;
    private SingleAnswerModel chillOrVisit;
    private SingleAnswerModel aboutFood;
    private SingleAnswerModel goOutAtNight;
    private SingleAnswerModel sport;
}