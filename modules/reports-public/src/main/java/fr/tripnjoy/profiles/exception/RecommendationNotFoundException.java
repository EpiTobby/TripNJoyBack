package fr.tripnjoy.profiles.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class RecommendationNotFoundException extends EntityNotFoundException {
    public RecommendationNotFoundException() {
    }

    public RecommendationNotFoundException(String message) {
        super(message);
    }
}
