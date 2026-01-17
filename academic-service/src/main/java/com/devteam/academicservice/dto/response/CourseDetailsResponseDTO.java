package com.devteam.academicservice.dto.response;

import com.devteam.academicservice.model.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CourseDetailsResponseDTO {

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

    @JsonProperty("course_description")
    private String description;

    @JsonProperty("course_invited_code")
    private String invitedCode;

    @JsonProperty("members")
    private List<Member> members;

    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonProperty("last_modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

}
