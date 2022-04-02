package fr.tobby.tripnjoyback.model.request.anwsers;

public enum ChillOrVisitAnswer {
    CHILL,
    VISIT,
    NO_PREFERENCE;

    public static ChillOrVisitAnswer of(String value){
        switch(value){
            case "CHILL":
                return CHILL;
            case "VISIT":
                return VISIT;
            default:
                return NO_PREFERENCE;
        }
    }
}
