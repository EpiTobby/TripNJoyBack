package fr.tripnjoy.groups.exception;

public class JoinGroupFailedException extends RuntimeException{
    public JoinGroupFailedException() {
    }

    public JoinGroupFailedException(String message) {
        super(message);
    }
}
