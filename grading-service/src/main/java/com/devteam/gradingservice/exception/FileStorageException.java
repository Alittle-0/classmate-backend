package com.devteam.gradingservice.exception;

import lombok.Getter;

@Getter
public class FileStorageException extends RuntimeException {

    private final FileStorageErrorCode errorCode;
    private final Object[] args;

    public FileStorageException(final FileStorageErrorCode errorCode, final Object... args) {
        super(getFormatterMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String getFormatterMessage(FileStorageErrorCode errorCode, Object[] args) {
        if (args != null && args.length > 0) {
            return String.format(errorCode.getDefaultMessage(), args);
        }
        return errorCode.getDefaultMessage();
    }
}
