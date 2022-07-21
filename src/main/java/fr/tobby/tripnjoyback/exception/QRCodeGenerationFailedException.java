package fr.tobby.tripnjoyback.exception;

public class QRCodeGenerationFailedException extends RuntimeException{
    public QRCodeGenerationFailedException() {
    }

    public QRCodeGenerationFailedException(String message) {
        super(message);
    }
}
