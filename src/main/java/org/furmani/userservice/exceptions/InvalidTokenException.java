package org.furmani.userservice.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
    public InvalidTokenException(String message, Exception e) {
        super(message, e);
    }
}
