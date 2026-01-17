package com.devteam.gradingservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Assignment related errors
    ASSIGNMENT_NOTFOUND("ASSIGNMENT_NOT_FOUND","Assignment not found with id: %s", HttpStatus.NOT_FOUND),
    ASSIGNMENT_ALREADY_EXISTS("ASSIGNMENT_ALREADY_EXISTS","Assignment with title '%s' already exists", HttpStatus.BAD_REQUEST),
    
    // Lecture related errors
    LECTURE_NOT_FOUND("LECTURE_NOT_FOUND","Lecture not found with id: %s", HttpStatus.NOT_FOUND),
    LECTURE_ALREADY_EXISTS("LECTURE_ALREADY_EXISTS","Lecture with title '%s' already exists in this course", HttpStatus.BAD_REQUEST),
    LECTURE_FILE_NOT_FOUND("LECTURE_FILE_NOT_FOUND","Lecture file not found with id: %s", HttpStatus.NOT_FOUND),
    
    // Submission related errors
    SUBMISSION_NOTFOUND("SUBMISSION_NOT_FOUND","Submission not found with id: %s", HttpStatus.NOT_FOUND),
    SUBMISSION_ALREADY_EXISTS("SUBMISSION_ALREADY_EXISTS","Submission already exists for this assignment", HttpStatus.BAD_REQUEST),
    SUBMISSION_DEADLINE_PASSED("SUBMISSION_DEADLINE_PASSED","Submission deadline has passed", HttpStatus.BAD_REQUEST),
    SUBMISSION_ACCESS_DENIED("SUBMISSION_ACCESS_DENIED","You do not have permission to delete this submission: %s", HttpStatus.FORBIDDEN),
    
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
