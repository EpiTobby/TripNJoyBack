package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.ProfileAnswersEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ProfileModel implements IProfile {
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
    private RangeAnswerModel groupSize;
    private ChillOrVisitAnswer chillOrVisit;
    private AboutFoodAnswer aboutFood;
    private YesNoAnswer goOutAtNight;
    private YesNoAnswer sport;
    private boolean isActive;
    private Instant createdDate;

    public static ProfileModel of(ProfileEntity profileEntity, ProfileAnswersEntity profileAnswersEntity){
        return ProfileModel.builder()
                           .id(profileEntity.getId())
                           .availabilities(profileAnswersEntity.getAvailabilities().stream().map(a -> AvailabilityAnswerModel.of(a.getStartDate(), a.getEndDate())).toList())
                           .duration(new RangeAnswerModel(profileAnswersEntity.getDurationMin(), profileAnswersEntity.getDurationMax()))
                           .budget(new RangeAnswerModel(profileAnswersEntity.getBudgetMin(), profileAnswersEntity.getBudgetMax()))
                           .destinationTypes(profileAnswersEntity.getDestinationTypes().stream().map(DestinationTypeAnswer::valueOf).toList())
                           .ages(new RangeAnswerModel(profileAnswersEntity.getAgeMin(), profileAnswersEntity.getAgeMax()))
                           .travelWithPersonFromSameCity(YesNoAnswer.of(profileAnswersEntity.getTravelWithPersonFromSameCity()))
                           .travelWithPersonFromSameCountry(YesNoAnswer.of(profileAnswersEntity.getTravelWithPersonFromSameCountry()))
                           .travelWithPersonSameLanguage(YesNoAnswer.of(profileAnswersEntity.getTravelWithPersonSameLanguage()))
                           .gender(GenderAnswer.valueOf(profileAnswersEntity.getGender()))
                           .groupSize(new RangeAnswerModel(profileAnswersEntity.getGroupSizeMin(), profileAnswersEntity.getGroupSizeMax()))
                           .chillOrVisit(ChillOrVisitAnswer.valueOf(profileAnswersEntity.getChillOrVisit()))
                           .aboutFood(AboutFoodAnswer.valueOf(profileAnswersEntity.getAboutFood()))
                           .goOutAtNight(YesNoAnswer.of(profileAnswersEntity.getGoOutAtNight()))
                           .sport(YesNoAnswer.of(profileAnswersEntity.getSport()))
                           .isActive(profileEntity.isActive())
                           .name(profileEntity.getName())
                           .createdDate(profileEntity.getCreatedDate())
                           .build();
    }

    public static ProfileModelBuilder builderOf(@NotNull ProfileModel other)
    {
        return ProfileModel.builder()
                           .id(0)
                           .availabilities(other.getAvailabilities().stream().map(AvailabilityAnswerModel::new).toList())
                           .duration(other.duration)
                           .budget(other.budget)
                           .destinationTypes(List.copyOf(other.destinationTypes))
                           .ages(other.ages)
                           .travelWithPersonFromSameCity(other.travelWithPersonFromSameCity)
                           .travelWithPersonFromSameCountry(other.travelWithPersonFromSameCountry)
                           .travelWithPersonSameLanguage(other.travelWithPersonSameLanguage)
                           .gender(other.gender)
                           .groupSize(other.groupSize)
                           .chillOrVisit(other.chillOrVisit)
                           .aboutFood(other.aboutFood)
                           .goOutAtNight(other.goOutAtNight)
                           .sport(other.sport)
                           .isActive(false)
                           .name(other.getName() + " Copy");

    }
}

