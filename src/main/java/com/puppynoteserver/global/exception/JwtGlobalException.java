package com.puppynoteserver.global.exception;

import lombok.Getter;

@Getter

public class JwtGlobalException extends RuntimeException{

    private String message;

    public JwtGlobalException(String message, Exception e) {
        super(message, e);
        this.message = message;
    }

    public JwtGlobalException(String message) {
        this.message = message;
    }
}
