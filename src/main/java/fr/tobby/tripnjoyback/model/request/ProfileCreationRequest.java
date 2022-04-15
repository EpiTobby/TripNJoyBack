package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.IProfile;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class ProfileCreationRequest implements IProfile {
    @NotNull
    private String name;
    @NotNull
    private List<AvailabilityAnswerModel> availabilities;
    @NotNull
    private RangeAnswerModel duration;
    @NotNull
    private RangeAnswerModel budget;
    @NotNull
    private List<DestinationTypeAnswer> destinationTypes;
    @NotNull
    private RangeAnswerModel ages;
    @NotNull
    private YesNoAnswer travelWithPersonFromSameCity;
    @NotNull
    private YesNoAnswer travelWithPersonFromSameCountry;
    @NotNull
    private YesNoAnswer travelWithPersonSameLanguage;
    @NotNull
    private GenderAnswer gender;
    @NotNull
    private RangeAnswerModel groupSize;
    @NotNull
    private ChillOrVisitAnswer chillOrVisit;
    @NotNull
    private AboutFoodAnswer aboutFood;
    @NotNull
    private YesNoAnswer goOutAtNight;
    @NotNull
    private YesNoAnswer sport;
}
