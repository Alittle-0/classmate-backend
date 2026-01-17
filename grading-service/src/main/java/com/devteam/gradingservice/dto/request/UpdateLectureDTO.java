package com.devteam.gradingservice.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UpdateLectureDTO {

    private String title;
    
    private String description;
}
