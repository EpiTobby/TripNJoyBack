package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String name;
    private List<AvailabilityAnswerModel> availabilities;
    private RangeAnswerModel duration;
    private RangeAnswerModel budget;
    private List<DestinationTypeAnswer> destinationTypes;
    private RangeAnswerModel ages;
    private YesNoAnswer travelWithPersonFromSameCity;
    private YesNoAnswer travelWithPersonFromSameCountry;
    private YesNoAnswer travelWithPersonSameLanguage;
    private GenderAnswer gender;
    private RangeAnswerModel groupSize;
    private ChillOrVisitAnswer chillOrVisit;
    private AboutFoodAnswer aboutFood;
    private YesNoAnswer goOutAtNight;
    private YesNoAnswer sport;
    private Boolean active;
}
