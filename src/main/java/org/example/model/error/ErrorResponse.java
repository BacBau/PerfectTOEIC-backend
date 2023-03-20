package org.example.model.error;

import lombok.Data;
import org.example.exception.EnglishExamException;

@Data
public class ErrorResponse {
    private String code;
    private String description;

    public ErrorResponse(EnglishExamException e) {
        this.code = e.getCode();
        this.description = e.getDescription();
    }

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.code();
        this.description = errorCode.description();
    }
}
