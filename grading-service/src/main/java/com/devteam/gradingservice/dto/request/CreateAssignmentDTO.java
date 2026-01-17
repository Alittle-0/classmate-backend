package com.devteam.gradingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateAssignmentDTO {

    @NotBlank(message = "VALIDATION.ASSIGNMENT.TITLE.NOT_BLANK")
    private String title;

    @NotBlank(message = "VALIDATION.ASSIGNMENT.DESCRIPTION.NOT_BLANK")
    private String description;

    @NotBlank(message = "VALIDATION.ASSIGNMENT.COURSE_ID.NOT_BLANK")
    private String courseId;

    @NotNull(message = "VALIDATION.ASSIGNMENT.SUBMISSION_DATE.NOT_BLANK")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDate;

}
