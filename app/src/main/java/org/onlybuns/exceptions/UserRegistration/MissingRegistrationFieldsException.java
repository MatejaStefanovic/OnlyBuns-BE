package org.onlybuns.exceptions.UserRegistration;

public class MissingRegistrationFieldsException extends RuntimeException {
    public MissingRegistrationFieldsException(String message) {
        super(message);
    }
}
