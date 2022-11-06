package fr.tripnjoy.profiles.model.answer;

import fr.tripnjoy.users.api.model.Gender;

public enum GenderAnswer implements StaticAnswerModel {
    MALE(Gender.MALE),
    FEMALE(Gender.FEMALE),
    MIXED(Gender.NOT_SPECIFIED);

    private final Gender gender;

    GenderAnswer(final Gender gender)
    {
        this.gender = gender;
    }

    public Gender toGender()
    {
        return gender;
    }
}
