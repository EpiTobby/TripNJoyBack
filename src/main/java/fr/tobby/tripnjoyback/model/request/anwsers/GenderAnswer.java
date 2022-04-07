package fr.tobby.tripnjoyback.model.request.anwsers;

import fr.tobby.tripnjoyback.model.Gender;

public enum GenderAnswer implements AnswerModel{
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
