package com.devteam.gradingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAssignmentDTO {

    @JsonProperty("assignment_id")
    private String id;

    @JsonProperty("assignment_title")
    private String title;

    @JsonProperty("assignment_description")
    private String description;

    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("create_by")
    private String createBy;

    @JsonProperty("submission_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDate;

    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    // For STUDENT: their own submission (null if not submitted)
    @JsonProperty("my_submission")
    private SubmissionResponse mySubmission;

    // For TEACHER: all submissions for this assignment
    @JsonProperty("submissions")
    private List<SubmissionResponse> submissions;
}
