package com.psr.awscognitowithoutoauth.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ExceptionResponse {
    private Integer status;
    private String message;
    private String timeStamp;
}