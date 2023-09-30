package com.hackyeah.sl.backend.exception.domain;

public class PasswordsDontMatchException extends Exception{
    public PasswordsDontMatchException(String message) {
        super(message);
    }
}
