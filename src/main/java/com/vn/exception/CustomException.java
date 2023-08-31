package com.vn.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException{
    private String errorCode;
    private int status;

    public CustomException(
            String message,
            String errorCode,
            int status
    ){
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
