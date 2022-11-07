package fr.tripnjoy.chat.exception;

public class SurveyVoteException extends RuntimeException{
    public SurveyVoteException() {
    }

    public SurveyVoteException(String message) {
        super(message);
    }
}
