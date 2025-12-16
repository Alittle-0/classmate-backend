package com.devteam.gradingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    COURSE_NOTFOUND("COURSE_NOT_FOUND","Course not found with id %s", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_EXISTS("COURSE_ALREADY_EXISTS","Course with code %s already exists", HttpStatus.BAD_REQUEST),

    //

    USER_NOTFOUND("USER_NOT_FOUND","User not found with id %s", HttpStatus.NOT_FOUND),

    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "New password mismatch confirm password", HttpStatus.BAD_REQUEST),

    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Password mismatch confirm password", HttpStatus.BAD_REQUEST),

    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Mismatching current password", HttpStatus.BAD_REQUEST),

    ACCOUNT_ALREADY_ACTIVATED("ACCOUNT_ALREADY_ACTIVATED", "Account with id %s is already activated", HttpStatus.BAD_REQUEST),

    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account with id %s is already deactivated", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_ROLE("INVALID_ROLE", "Invalid role", HttpStatus.BAD_REQUEST),
    ERROR_USER_DISABLE("ERROR_USER_DISABLE", "User is disable", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Email and / or password is incorrect", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "username not found", HttpStatus.NOT_FOUND),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("INVALID_TOKEN", "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", "Invalid token type. Expected %s but got %s", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "Refresh token has expired", HttpStatus.UNAUTHORIZED),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code, final String defaultMessage, final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
