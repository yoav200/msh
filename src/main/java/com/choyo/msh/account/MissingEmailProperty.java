package com.choyo.msh.account;

public class MissingEmailProperty extends Exception {
    public MissingEmailProperty() {
        super("Missing required email property");
    }
}
