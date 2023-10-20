package com.psr.awscognitowithoutoauth.exceptionHandler;

public class MyAuthenticationExceptionHandler extends  RuntimeException{
    public MyAuthenticationExceptionHandler(String message) {
        super(message);
    }
}
