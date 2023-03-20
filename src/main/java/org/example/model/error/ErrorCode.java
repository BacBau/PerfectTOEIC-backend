package org.example.model.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ACCESS_DENIED("CD-010", "Xin lỗi, bạn không có quyền truy cập!", HttpStatus.FORBIDDEN),
    BAD_REQUEST("CD-010", "Yêu cầu của bạn chưa đúng!!", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("CD-11", "User not found!", HttpStatus.NOT_FOUND),
    BANNED_PERFORM_ACTION("CD-CLIENT-403", "Banded perform action!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("UNAUTHORIZED", "unauthorized", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER("INTERNAL-SERVER", "internal server!", HttpStatus.INTERNAL_SERVER_ERROR),
    POST_NOT_FOUND("POST_NOT_FOUND", "Post not found!", HttpStatus.NOT_FOUND),
    MINI_TEST_NOT_FOUND("MINI-TEST-NOT-FOUND", "mini test not found!", HttpStatus.NOT_FOUND);

    private String code;
    private String description;
    private HttpStatus httpStatus;

    ErrorCode(String code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return this.code;
    }

    public String description() {
        return this.description;
    }

    public HttpStatus httpStatus() {
        return this.httpStatus;
    }
}
