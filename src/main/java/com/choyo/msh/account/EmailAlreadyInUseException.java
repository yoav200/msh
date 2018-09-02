package com.choyo.msh.account;

public class EmailAlreadyInUseException extends Exception {
    public EmailAlreadyInUseException(String email) {
        super("The email '" + email + "' is already in use.");
    }
}