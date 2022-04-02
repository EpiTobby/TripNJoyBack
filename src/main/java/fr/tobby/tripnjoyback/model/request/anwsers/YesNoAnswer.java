package fr.tobby.tripnjoyback.model.request.anwsers;

public enum YesNoAnswer implements AnswerModel{
    YES,
    NO,
    NO_PREFERENCE;

    public Boolean toBoolean(){
        switch (this) {
            case YES:
                return true;
            case NO:
                return false;
            default:
                return null;
        }
    }

    public static YesNoAnswer of(Boolean bool){
        if (bool == null)
            return NO_PREFERENCE;
        return bool ? YES : NO;
    }
}
