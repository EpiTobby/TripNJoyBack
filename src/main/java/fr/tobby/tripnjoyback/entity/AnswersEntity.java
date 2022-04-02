package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Profiles")
public class AnswersEntity {
    @NotNull
    private long profileId;
    private String startDate;
    private String endDate;
    private int durationMin;
    private int durationMax;
    private int budgetMin;
    private int budgetMax;
    private List<String> destinationTypes;
    private int ageMin;
    private int ageMax;
    private Boolean travelWithPersonFromSameCity;
    private Boolean travelWithPersonFromSameCountry;
    private Boolean travelWithPersonSameLanguage;
    private String gender;
    private int groupSizeMin;
    private int groupSizeMax;
    private String chillOrVisit;
    private String aboutFood;
    private Boolean goOutAtNight;
    private Boolean sport;

    public AnswersEntity(long profileId, ProfileCreationRequest profileModel){
        this.profileId = profileId;
        setAvailability(profileModel.getAvailability());
        this.durationMin = profileModel.getDuration().getMinValue();
        this.durationMax = profileModel.getDuration().getMaxValue();
        this.budgetMin = profileModel.getBudget().getMinValue();
        this.budgetMax = profileModel.getBudget().getMaxValue();
        this.destinationTypes = profileModel.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList();
        this.ageMin = profileModel.getAges().getMinValue();
        this.ageMax = profileModel.getAges().getMaxValue();
        this.travelWithPersonFromSameCity = profileModel.getTravelWithPersonFromSameCity().toBoolean();
        this.travelWithPersonFromSameCountry = profileModel.getTravelWithPersonFromSameCountry().toBoolean();
        this.travelWithPersonSameLanguage = profileModel.getTravelWithPersonSameLanguage().toBoolean();
        this.gender = profileModel.getGender().toString();
        this.groupSizeMin = profileModel.getGroupSize().getMinValue();
        this.groupSizeMax = profileModel.getGroupSize().getMaxValue();
        this.chillOrVisit = profileModel.getChillOrVisit().toString();
        this.aboutFood = profileModel.getAboutFood().toString();
        this.goOutAtNight = profileModel.getGoOutAtNight().toBoolean();
        this.sport = profileModel.getSport().toBoolean();
    }

    public static AnswersEntity of(ProfileModel profileModel){
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        return AnswersEntity.builder()
        .startDate(dateFormat.format(profileModel.getAvailability().getStartDate()))
        .endDate(dateFormat.format(profileModel.getAvailability().getEndDate()))
        .durationMin(profileModel.getDuration().getMinValue())
        .durationMax(profileModel.getDuration().getMaxValue())
        .budgetMin(profileModel.getBudget().getMinValue())
        .budgetMax(profileModel.getBudget().getMaxValue())
        .destinationTypes(profileModel.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList())
        .ageMin(profileModel.getAges().getMinValue())
        .ageMax(profileModel.getAges().getMaxValue())
        .travelWithPersonFromSameCity(profileModel.getTravelWithPersonFromSameCity().toBoolean())
        .travelWithPersonFromSameCountry(profileModel.getTravelWithPersonFromSameCountry().toBoolean())
        .travelWithPersonSameLanguage(profileModel.getTravelWithPersonSameLanguage().toBoolean())
        .gender(profileModel.getGender().toString())
        .groupSizeMin(profileModel.getGroupeSize().getMinValue())
        .groupSizeMax(profileModel.getGroupeSize().getMaxValue())
        .chillOrVisit(profileModel.getChillOrVisit().toString())
        .aboutFood(profileModel.getAboutFood().toString())
        .goOutAtNight(profileModel.getGoOutAtNight().toBoolean())
        .sport(profileModel.getSport().toBoolean()).build();
    }

    public void update(ProfileUpdateRequest profileUpdateRequest){
        if (profileUpdateRequest.getAvailability() != null)
            setAvailability(profileUpdateRequest.getAvailability());
        if (profileUpdateRequest.getDuration() != null) {
            this.durationMin = profileUpdateRequest.getDuration().getMinValue();
            this.durationMax = profileUpdateRequest.getDuration().getMaxValue();
        }
        if (profileUpdateRequest.getBudget() != null){
            this.budgetMin = profileUpdateRequest.getBudget().getMinValue();
            this.budgetMax = profileUpdateRequest.getBudget().getMaxValue();
        }
        if (profileUpdateRequest.getDestinationTypes() != null)
            this.destinationTypes = profileUpdateRequest.getDestinationTypes().stream().map(DestinationTypeAnswer::toString).toList();
        if (profileUpdateRequest.getAges() != null){
            this.ageMin = profileUpdateRequest.getAges().getMinValue();
            this.ageMax = profileUpdateRequest.getAges().getMaxValue();
        }
        if (profileUpdateRequest.getTravelWithPersonFromSameCity() != null)
            this.travelWithPersonFromSameCity = profileUpdateRequest.getTravelWithPersonFromSameCity().toBoolean();
        if (profileUpdateRequest.getTravelWithPersonFromSameCountry() != null)
            this.travelWithPersonFromSameCountry = profileUpdateRequest.getTravelWithPersonFromSameCountry().toBoolean();
        if (profileUpdateRequest.getTravelWithPersonSameLanguage() != null)
            this.travelWithPersonSameLanguage = profileUpdateRequest.getTravelWithPersonSameLanguage().toBoolean();
        if (profileUpdateRequest.getGender() != null)
            this.gender = profileUpdateRequest.getGender().toString();
        if (profileUpdateRequest.getGroupeSize() != null) {
            this.groupSizeMin = profileUpdateRequest.getGroupeSize().getMinValue();
            this.groupSizeMax = profileUpdateRequest.getGroupeSize().getMaxValue();
        }
        if (profileUpdateRequest.getChillOrVisit() != null)
            this.chillOrVisit = profileUpdateRequest.getChillOrVisit().toString();
        if (profileUpdateRequest.getAboutFood() != null)
            this.aboutFood = profileUpdateRequest.getAboutFood().toString();
        if (profileUpdateRequest.getGoOutAtNight() != null)
            this.goOutAtNight = profileUpdateRequest.getGoOutAtNight().toBoolean();
        if (profileUpdateRequest.getSport() != null)
            this.sport = profileUpdateRequest.getSport().toBoolean();
    }

    public void setAvailability(AvailabilityAnswerModel availability){
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        this.startDate = dateFormat.format(availability.getStartDate());
        this.endDate = dateFormat.format(availability.getEndDate());
    }
}
