package com.psr.awscognitowithoutoauth.exceptionHandler.handler;

import com.psr.awscognitowithoutoauth.exceptionHandler.MyAuthenticationExceptionHandler;
import com.psr.awscognitowithoutoauth.helper.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Handler {
    @ExceptionHandler(MyAuthenticationExceptionHandler.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception exc) {
        ExceptionResponse error = new ExceptionResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
