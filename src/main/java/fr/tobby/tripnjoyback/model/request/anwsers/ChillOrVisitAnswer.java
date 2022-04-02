package fr.tobby.tripnjoyback.model.request.anwsers;

public enum ChillOrVisitAnswer {
    CHILL,
    VISIT;

    public static ChillOrVisitAnswer of(String value){
        switch(value){
            case "CHILL":
                return CHILL;
            case "VISIT":
                return VISIT;
            default:
                return null;
        }
    }
}
