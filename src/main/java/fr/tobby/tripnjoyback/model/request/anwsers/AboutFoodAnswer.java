package fr.tobby.tripnjoyback.model.request.anwsers;

public enum AboutFoodAnswer implements AnswerModel{
    COOKING,
    RESTAURANT;

    public static AboutFoodAnswer of(String value){
        switch(value){
            case "COOKING":
                return COOKING;
            case "RESTAURANT":
                return RESTAURANT;
            default:
                return null;
        }
    }
}
