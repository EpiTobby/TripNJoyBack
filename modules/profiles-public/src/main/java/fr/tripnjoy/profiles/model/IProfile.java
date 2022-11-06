package fr.tripnjoy.profiles.model;

import fr.tripnjoy.profiles.model.answer.AvailabilityAnswerModel;
import fr.tripnjoy.profiles.model.answer.GenderAnswer;
import fr.tripnjoy.profiles.model.answer.YesNoAnswer;

import java.util.List;

public interface IProfile {
    String getName();

    List<AvailabilityAnswerModel> getAvailabilities();

    RangeAnswerModel getDuration();

    RangeAnswerModel getBudget();

    List<DestinationTypeAnswer> getDestinationTypes();

    RangeAnswerModel getAges();

    YesNoAnswer getTravelWithPersonFromSameCity();

    YesNoAnswer getTravelWithPersonFromSameCountry();

    YesNoAnswer getTravelWithPersonSameLanguage();

    GenderAnswer getGender();

    RangeAnswerModel getGroupSize();

    ChillOrVisitAnswer getChillOrVisit();

    AboutFoodAnswer getAboutFood();

    YesNoAnswer getGoOutAtNight();

    YesNoAnswer getSport();
}
