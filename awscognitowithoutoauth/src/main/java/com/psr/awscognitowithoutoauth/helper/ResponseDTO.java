package com.psr.awscognitowithoutoauth.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.stereotype.Component;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ResponseDTO<T> {

    private T data;
    private int status;
    private String message;

}

