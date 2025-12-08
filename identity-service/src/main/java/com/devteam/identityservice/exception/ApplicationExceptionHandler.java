package com.devteam.identityservice.exception;

import com.devteam.identityservice.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = "com.devteam.identityservice")
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(final BusinessException exception) {
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .code(exception.getErrorCode().getCode())
                .message(exception.getMessage())
                .build();

        log.info("Business exception: {}", exception.getMessage());
        log.debug(exception.getMessage(), exception);

        return ResponseEntity.status(exception.getErrorCode()
                        .getStatus() != null ?
                        exception.getErrorCode().getStatus() :
                        HttpStatus.BAD_REQUEST)
                .body(body);

    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(final DisabledException exception) {
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .code(ErrorCode.ERROR_USER_DISABLE.getCode())
                .message(ErrorCode.ERROR_USER_DISABLE.getDefaultMessage())
                .build();

        return ResponseEntity.status(ErrorCode.ERROR_USER_DISABLE
                        .getStatus())
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(final BadCredentialsException exception) {
        log.debug(exception.getMessage(), exception);
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .message(ErrorCode.BAD_CREDENTIALS.getDefaultMessage())
                .code(ErrorCode.BAD_CREDENTIALS.getCode())
                .build();
        return ResponseEntity.status(ErrorCode.BAD_CREDENTIALS
                        .getStatus())
                .body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(final UsernameNotFoundException exception) {
        log.debug(exception.getMessage(), exception);
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .message(ErrorCode.USERNAME_NOT_FOUND.getDefaultMessage())
                .code(ErrorCode.USERNAME_NOT_FOUND.getCode())
                .build();
        return ResponseEntity.status(ErrorCode.USERNAME_NOT_FOUND
                        .getStatus())
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(MethodArgumentNotValidException exception) {
        final List<ErrorResponseDTO.ValidationError> errorList = new ArrayList<>();
        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String errorCode = error.getDefaultMessage();
                    errorList.add(ErrorResponseDTO.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(errorCode)
                            .build());
                });
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .validationErrorList(errorList).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(final Exception exception) {
        log.debug(exception.getMessage(), exception);
        final ErrorResponseDTO body = ErrorResponseDTO.builder()
                .message(ErrorCode.INTERNAL_EXCEPTION.getDefaultMessage())
                .code(ErrorCode.INTERNAL_EXCEPTION.getCode())
                .build();
        return ResponseEntity.status(ErrorCode.INTERNAL_EXCEPTION
                        .getStatus())
                .body(body);
    }

}
