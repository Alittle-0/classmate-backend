package com.devteam.academicservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateCourseDTO {

    @NotBlank(message = "VALIDATION.COURSE.NAME.NOT_BLANK")
    private String name;

    @NotBlank(message = "VALIDATION.COURSE.DESCRIPTION.NOT_BLANK")
    private String description;

    private String subTeacherId;

    private String subTeacherName;

}
