package com.example.javaserver.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthExeption extends AuthenticationException {
    public JwtAuthExeption(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtAuthExeption(String msg) {
        super(msg);
    }
}
