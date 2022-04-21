package fr.tobby.tripnjoyback.entity;

import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.model.request.anwsers.DestinationTypeAnswer;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Profiles")
public class AnswersEntity {
    @Id
    private String id;
    private long profileId;
    @Setter
    private List<AvailabiltyEntity> availabilities;
    @Setter
    private int durationMin;
    @Setter
    private int durationMax;
    @Setter
    private int budgetMin;
    @Setter
    private int budgetMax;
    @Setter
    private List<String> destinationTypes;
    @Setter
    private int ageMin;
    @Setter
    private int ageMax;
    @Setter
    private Boolean travelWithPersonFromSameCity;
    @Setter
    private Boolean travelWithPersonFromSameCountry;
    @Setter
    private Boolean travelWithPersonSameLanguage;
    @Setter
    private String gender;
    @Setter
    private int groupSizeMin;
    @Setter
    private int groupSizeMax;
    @Setter
    private String chillOrVisit;
    @Setter
    private String aboutFood;
    @Setter
    private Boolean goOutAtNight;
    @Setter
    private Boolean sport;

    public static AnswersEntity of(ProfileModel profileModel){
        DateFormat dateFormat = new SimpleDateFormat(AvailabilityAnswerModel.DATE_FORMAT);
        return AnswersEntity.builder()
                            .availabilities(profileModel.getAvailabilities().stream().map(a -> new AvailabiltyEntity(dateFormat.format(a.getStartDate()),dateFormat.format(a.getEndDate()))).toList())
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
                            .groupSizeMin(profileModel.getGroupSize().getMinValue())
                            .groupSizeMax(profileModel.getGroupSize().getMaxValue())
                            .chillOrVisit(profileModel.getChillOrVisit().toString())
                            .aboutFood(profileModel.getAboutFood().toString())
                            .goOutAtNight(profileModel.getGoOutAtNight().toBoolean())
                            .sport(profileModel.getSport().toBoolean()).build();
    }
}
