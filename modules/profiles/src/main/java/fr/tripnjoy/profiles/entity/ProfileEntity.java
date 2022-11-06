package fr.tripnjoy.profiles.entity;

import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.answer.*;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name  = "profiles")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name")
    private String name;

    @Column(name = "active")
    @Setter
    private boolean active;

    private Instant createdDate;

    public ProfileModel toModel(AnswersEntity answersEntity){
        return ProfileModel.builder()
                           .id(this.getId())
                           .availabilities(answersEntity.getAvailabilities().stream().map(a -> AvailabilityAnswerModel.of(a.getStartDate(), a.getEndDate())).toList())
                           .duration(new RangeAnswerModel(answersEntity.getDurationMin(), answersEntity.getDurationMax()))
                           .budget(new RangeAnswerModel(answersEntity.getBudgetMin(), answersEntity.getBudgetMax()))
                           .destinationTypes(answersEntity.getDestinationTypes().stream().map(DestinationTypeAnswer::valueOf).toList())
                           .ages(new RangeAnswerModel(answersEntity.getAgeMin(), answersEntity.getAgeMax()))
                           .travelWithPersonFromSameCity(YesNoAnswer.of(answersEntity.getTravelWithPersonFromSameCity()))
                           .travelWithPersonFromSameCountry(YesNoAnswer.of(answersEntity.getTravelWithPersonFromSameCountry()))
                           .travelWithPersonSameLanguage(YesNoAnswer.of(answersEntity.getTravelWithPersonSameLanguage()))
                           .gender(GenderAnswer.valueOf(answersEntity.getGender()))
                           .groupSize(new RangeAnswerModel(answersEntity.getGroupSizeMin(), answersEntity.getGroupSizeMax()))
                           .chillOrVisit(ChillOrVisitAnswer.valueOf(answersEntity.getChillOrVisit()))
                           .aboutFood(AboutFoodAnswer.valueOf(answersEntity.getAboutFood()))
                           .goOutAtNight(YesNoAnswer.of(answersEntity.getGoOutAtNight()))
                           .sport(YesNoAnswer.of(answersEntity.getSport()))
                           .isActive(this.isActive())
                           .name(this.getName())
                           .createdDate(this.getCreatedDate())
                           .build();
    }
}
