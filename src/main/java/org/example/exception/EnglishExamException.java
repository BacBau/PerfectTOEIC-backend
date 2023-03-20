package org.example.exception;

import org.example.model.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class EnglishExamException extends RuntimeException {
    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    public EnglishExamException(ErrorCode errorCode) {
        super(errorCode.description());
        this.code = errorCode.code();
        this.description = errorCode.description();
        this.httpStatus = errorCode.httpStatus();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
