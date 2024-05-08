package at.ac.uibk.fiba.ladder3ca.business.service;

public class EntityMissingException extends Exception {

    public EntityMissingException() {
        super("Cannot find text.");
    }

    public EntityMissingException(String message) {
        super(message);
    }

    public EntityMissingException(String message, Throwable cause) {
        super(message, cause);
    }
}
