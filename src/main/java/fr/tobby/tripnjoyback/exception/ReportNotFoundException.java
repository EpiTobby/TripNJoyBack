package fr.tobby.tripnjoyback.exception;

public class ReportNotFoundException extends EntityNotFoundException{
    public ReportNotFoundException() {
    }

    public ReportNotFoundException(String message) {
        super(message);
    }
}
