package com.devteam.academicservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CourseResponse {

    @JsonProperty("course_id")
    private String id;

    @JsonProperty("course_name")
    private String name;

    @JsonProperty("teacher_id")
    private String teacherId;

    @JsonProperty("teacher_firstname")
    private String teacherFirstname;

    @JsonProperty("teacher_lastname")
    private String teacherLastname;

    @JsonProperty("sub_teacher_id")
    private String subTeacherId;

    @JsonProperty("sub_teacher_name")
    private String subTeacherName;

}
