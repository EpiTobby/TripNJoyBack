package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.AnswersEntity;
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
    private RangeAnswerModel groupeSize;
    private ChillOrVisitAnswer chillOrVisit;
    private AboutFoodAnswer aboutFood;
    private YesNoAnswer goOutAtNight;
    private YesNoAnswer sport;
    private long userId;
    private boolean isActive;

    public static ProfileModel of(ProfileEntity profileEntity, AnswersEntity answersEntity){
        return ProfileModel.builder()
                .id(profileEntity.getId())
                .availabilities(answersEntity.getAvailabilities().stream().map(a -> AvailabilityAnswerModel.of(a.getStartDate(),a.getEndDate())).toList())
                .duration(new RangeAnswerModel(answersEntity.getDurationMin(), answersEntity.getDurationMax()))
                .budget(new RangeAnswerModel(answersEntity.getBudgetMin(), answersEntity.getBudgetMax()))
                .destinationTypes(answersEntity.getDestinationTypes().stream().map(DestinationTypeAnswer::valueOf).toList())
                .ages(new RangeAnswerModel(answersEntity.getAgeMin(), answersEntity.getAgeMax()))
                .travelWithPersonFromSameCity(YesNoAnswer.of(answersEntity.getTravelWithPersonFromSameCity()))
                .travelWithPersonFromSameCountry(YesNoAnswer.of(answersEntity.getTravelWithPersonFromSameCountry()))
                .travelWithPersonSameLanguage(YesNoAnswer.of(answersEntity.getTravelWithPersonSameLanguage()))
                .gender(GenderAnswer.valueOf(answersEntity.getGender()))
                .groupeSize(new RangeAnswerModel(answersEntity.getGroupSizeMin(), answersEntity.getGroupSizeMax()))
                .chillOrVisit(ChillOrVisitAnswer.valueOf(answersEntity.getChillOrVisit()))
                .aboutFood(AboutFoodAnswer.valueOf(answersEntity.getAboutFood()))
                .goOutAtNight(YesNoAnswer.of(answersEntity.getGoOutAtNight()))
                .sport(YesNoAnswer.of(answersEntity.getSport()))
                .userId(profileEntity.getUserId())
                .isActive(profileEntity.isActive())
                .name(profileEntity.getName())
                .build();
    }
}