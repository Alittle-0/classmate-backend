package com.devteam.gradingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileStorageErrorCode {

    FILE_STORAGE_EXCEPTION("FILE_STORAGE_EXCEPTION", "Could not store file. Please try again!", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found: %s", HttpStatus.NOT_FOUND),
    INVALID_FILE_PATH("INVALID_FILE_PATH", "Invalid file path: %s", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "File size exceeds maximum limit of %s MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("INVALID_FILE_TYPE", "File type not allowed: %s", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    FileStorageErrorCode(final String code, final String defaultMessage, final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
