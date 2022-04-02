package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationModel;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private int groupeSizeMin;
    private int groupeSizeMax;
    private String chillOrVisit;
    private String aboutFood;
    private Boolean goOutAtNight;
    private Boolean sport;

    public AnswersEntity(long profileId, ProfileCreationModel profileModel){
        this.profileId = profileId;
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        this.startDate = dateFormat.format(profileModel.getAvailability().getStartDate());
        this.endDate = dateFormat.format(profileModel.getAvailability().getEndDate());
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
        this.groupeSizeMin = profileModel.getGroupeSize().getMinValue();
        this.groupeSizeMax = profileModel.getGroupeSize().getMaxValue();
        this.chillOrVisit = profileModel.getChillOrVisit().toString();
        this.aboutFood = profileModel.getAboutFood().toString();
        this.goOutAtNight = profileModel.getGoOutAtNight().toBoolean();
        this.sport = profileModel.getSport().toBoolean();
    }
}
