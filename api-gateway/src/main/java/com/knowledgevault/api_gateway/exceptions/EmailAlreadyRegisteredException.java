package com.knowledgevault.api_gateway.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException() {
        super("An account already exists for this email.");
    }
}
