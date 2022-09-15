package fr.tobby.tripnjoyback.exception;

public class JoinGroupFailedException extends RuntimeException{
    public JoinGroupFailedException() {
    }

    public JoinGroupFailedException(String message) {
        super(message);
    }
}
