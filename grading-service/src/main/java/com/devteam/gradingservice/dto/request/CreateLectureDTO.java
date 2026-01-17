package com.devteam.gradingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateLectureDTO {

    @NotBlank(message = "VALIDATION.LECTURE.TITLE.NOT_BLANK")
    private String title;

    private String description;

    @NotBlank(message = "VALIDATION.LECTURE.COURSE_ID.NOT_BLANK")
    private String courseId;
}
