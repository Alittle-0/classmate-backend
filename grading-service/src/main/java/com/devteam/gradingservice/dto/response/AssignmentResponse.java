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
public class AssignmentResponse {

    @JsonProperty("assignment_id")
    private String id;

    @JsonProperty("assignment_title")
    private String title;

    @JsonProperty("course_id")
    private String courseId;

    @JsonProperty("create_by")
    private String createBy;

}
