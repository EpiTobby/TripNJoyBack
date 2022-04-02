package fr.tobby.tripnjoyback.model.request.anwsers;

public enum DestinationTypeAnswer implements AnswerModel {
    CITY,
    COUNTRYSIDE,
    BEACH,
    MOUNTAIN;

    public static DestinationTypeAnswer of(String value){
        switch(value){
            case "CITY":
                return CITY;
            case "COUNTRYSIDE":
                return COUNTRYSIDE;
            case "BEACH":
                return BEACH;
            case "MOUNTAIN":
                return MOUNTAIN;
        }
        return null;
    }
}
