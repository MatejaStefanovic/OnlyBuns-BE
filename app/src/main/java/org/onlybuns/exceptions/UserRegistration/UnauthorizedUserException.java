package org.onlybuns.exceptions.UserRegistration;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
