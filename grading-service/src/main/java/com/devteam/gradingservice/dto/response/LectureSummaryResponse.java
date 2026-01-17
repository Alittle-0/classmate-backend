package com.devteam.gradingservice.dto.response;

import lombok.*;

/**
 * Summary response for listing lectures - contains only essential info
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LectureSummaryResponse {

    private String id;
    private String title;
}
