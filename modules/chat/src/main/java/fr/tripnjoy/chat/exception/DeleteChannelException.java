package fr.tripnjoy.chat.exception;

public class DeleteChannelException extends RuntimeException{
    public DeleteChannelException() {
    }

    public DeleteChannelException(String message) {
        super(message);
    }
}
