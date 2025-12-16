package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.request.CreateAssignmentDTO;
import com.devteam.gradingservice.dto.response.GetAssignmentDTO;
import com.devteam.gradingservice.model.Assignment;
import com.devteam.gradingservice.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;

    public void createAssignment(CreateAssignmentDTO request){
//        check valid
        Assignment savedAssignment = this.assignmentMapper.toAssignment(request);
        this.assignmentRepository.save(savedAssignment);
    }

    public GetAssignmentDTO getAssignmentById(String assignmentId){
        Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow();
        return GetAssignmentDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .courseId(assignment.getCourseId())
                .createBy(assignment.getCreateBy())
                .submissionDate(assignment.getSubmissionDate())
                .createdDate(assignment.getCreatedDate())
                .lastModifiedDate(assignment.getLastModifiedDate())
                .build();

    }

    public List<Assignment> getAllAssignmentByCourseId(String courseId){
        return this.assignmentRepository.findAllByCourseId(courseId);
    }

    public void deleteAssignmentById(String assignmentId){
        this.assignmentRepository.deleteById(assignmentId);
    }

    public void updateAssignment(Assignment assignment){
        this.assignmentRepository.save(assignment);
    }

}
