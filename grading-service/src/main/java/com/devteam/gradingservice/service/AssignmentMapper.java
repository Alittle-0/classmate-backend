package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.request.CreateAssignmentDTO;
import com.devteam.gradingservice.dto.request.UpdateAssignmentRequestDTO;
import com.devteam.gradingservice.model.Assignment;
import com.devteam.gradingservice.security.SecurityUtils;
import com.devteam.gradingservice.security.UserPrincipal;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AssignmentMapper {

    public void mergeAssignment(Assignment assignment, UpdateAssignmentRequestDTO requestDTO) {
        if (StringUtils.isNotBlank(requestDTO.getTitle()) && !requestDTO.getTitle().equals(assignment.getTitle())) assignment.setTitle(requestDTO.getTitle());
        if (StringUtils.isNotBlank(requestDTO.getDescription()) && !requestDTO.getDescription().equals(assignment.getDescription())) assignment.setDescription(requestDTO.getDescription());
        if (requestDTO.getSubmissionDate() != null && !requestDTO.getSubmissionDate().equals(assignment.getSubmissionDate())) assignment.setSubmissionDate(requestDTO.getSubmissionDate());
    }

    public Assignment toAssignment(CreateAssignmentDTO request){
        // Get current authenticated user
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        String createdBy = currentUser != null ? currentUser.getUserId() : "SYSTEM";
        
        return Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .courseId(request.getCourseId())
                .createBy(createdBy)
                .submissionDate(request.getSubmissionDate())
                .build();
    }

}
