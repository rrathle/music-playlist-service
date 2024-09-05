package com.amazon.ata.music.playlist.service.exceptions;

/**
 * Exception to throw when a
 */
public class InvalidAttributeChangeException extends InvalidAttributeException {
    private static final long serialVersionUID = -6251054687616428885L;

    public InvalidAttributeChangeException() {
        super();
    }

    ;

    public InvalidAttributeChangeException(String message) {
        super(message);
    }

    ;

    public InvalidAttributeChangeException(Throwable cause) {
        super(cause);
    }

    ;

    public InvalidAttributeChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}


