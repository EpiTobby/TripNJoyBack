package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
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
public class ProfileModel {
    private long id;
    private AvailabilityAnswerModel availability;
    private RangeAnswerModel duration;
    private RangeAnswerModel budget;
    private List<DestinationTypeAnswer> destinationTypes;
    private RangeAnswerModel ages;
    private YesNoAnswer travelWithPersonFromSameCity;
    private YesNoAnswer travelWithPersonFromSameCountry;
    private YesNoAnswer travelWithPersonSameLanguage;
    private GenderAnswer gender;
    private RangeAnswerModel groupeSize;
    private ChillOrVisitAnswer chillOrVisit;
    private AboutFoodAnswer aboutFood;
    private YesNoAnswer goOutAtNight;
    private YesNoAnswer sport;
    private long userId;
    private boolean isActive;

    public static ProfileModel of(ProfileEntity profileEntity){
        return new ProfileModel().builder()
                .id(profileEntity.getId())
                .userId(profileEntity.getUserId())
                .isActive(profileEntity.isActive())
                .build();
    }
}