package com.devteam.academicservice.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorResponseDTO {

    private String message;
    private String code;
    private List<ValidationError> validationErrorList;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class ValidationError {
        private String field;
        private String code;
        private String message;
    }

}
