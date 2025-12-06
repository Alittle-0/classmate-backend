package com.devteam.identityservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOTFOUND("USER_NOT_FOUND","User not found with id %s", HttpStatus.NOT_FOUND),

    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "New password mismatch confirm password", HttpStatus.BAD_REQUEST),

    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Mismatching current password", HttpStatus.BAD_REQUEST),

    ACCOUNT_ALREADY_ACTIVATED("ACCOUNT_ALREADY_ACTIVATED", "Account with id %s is already activated", HttpStatus.BAD_REQUEST),

    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account with id %s is already deactivated", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code, final String defaultMessage, final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
