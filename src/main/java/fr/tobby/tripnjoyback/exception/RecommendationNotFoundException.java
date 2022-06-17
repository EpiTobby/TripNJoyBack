package fr.tobby.tripnjoyback.exception;

public class RecommendationNotFoundException extends EntityNotFoundException{
    public RecommendationNotFoundException() {
    }

    public RecommendationNotFoundException(String message) {
        super(message);
    }
}
