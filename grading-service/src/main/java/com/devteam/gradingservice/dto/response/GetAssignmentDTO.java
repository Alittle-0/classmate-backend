package com.devteam.gradingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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


}
