package fr.tripnjoy.profiles.exception;

import fr.tripnjoy.common.exception.EntityNotFoundException;

public class ReportNotFoundException extends EntityNotFoundException {
    public ReportNotFoundException() {
    }

    public ReportNotFoundException(String message) {
        super(message);
    }
}
