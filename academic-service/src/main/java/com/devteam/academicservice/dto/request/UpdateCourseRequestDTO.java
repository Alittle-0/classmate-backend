package com.devteam.academicservice.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateCourseRequestDTO {

    private String name;

    private String description;

}
