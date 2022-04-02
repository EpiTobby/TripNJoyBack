package fr.tobby.tripnjoyback.model.request.anwsers;

public enum GenderAnswer implements AnswerModel{
    MALE,
    FEMALE,
    MIXED;

    public static GenderAnswer of(String value){
        switch (value){
            case "MALE":
                return MALE;
            case "FEMALE":
                return FEMALE;
            default:
                return MIXED;
        }
    }
}
