package com.devteam.academicservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Course related errors
    // Course related errors
    COURSE_NOTFOUND("COURSE_NOT_FOUND","Course not found with id: %s", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS("COURSE_ALREADY_EXISTS","Course with code '%s' already exists", HttpStatus.BAD_REQUEST),
    INVALID_MEMBER("INVALID_MEMBER", "You are not a member of course: %s", HttpStatus.FORBIDDEN),
    
    // General errors
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code, final String defaultMessage, final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
