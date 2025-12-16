package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.request.CreateAssignmentDTO;
import com.devteam.gradingservice.model.Assignment;
import org.springframework.stereotype.Service;

@Service
public class AssignmentMapper {

    public Assignment toAssignment(CreateAssignmentDTO request){
        return Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .courseId(request.getCourseId())
                .createBy(request.getCreateBy())
                .submissionDate(request.getSubmissionDate())
                .build();
    }

}
