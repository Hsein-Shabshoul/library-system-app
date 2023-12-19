package com.project.library.exception;

import javax.naming.AuthenticationException;

public class BadCredentialsE extends AuthenticationException {
    public BadCredentialsE(String message) {
        super(message);
    }
}

