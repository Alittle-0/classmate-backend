package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.request.CreateAssignmentDTO;
import com.devteam.gradingservice.dto.request.UpdateAssignmentRequestDTO;
import com.devteam.gradingservice.dto.response.AssignmentResponse;
import com.devteam.gradingservice.dto.response.GetAssignmentDTO;
import com.devteam.gradingservice.dto.response.SubmissionResponse;
import com.devteam.gradingservice.exception.BusinessException;
import com.devteam.gradingservice.exception.ErrorCode;
import com.devteam.gradingservice.model.Assignment;
import com.devteam.gradingservice.repository.AssignmentRepository;
import com.devteam.gradingservice.security.SecurityUtils;
import com.devteam.gradingservice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;
    private final SubmissionService submissionService;

    public void createAssignment(CreateAssignmentDTO request){
        checkAssignmentTitle(request.getTitle());

        Assignment savedAssignment = this.assignmentMapper.toAssignment(request);
        this.assignmentRepository.save(savedAssignment);
    }

    /**
     * Get assignment by ID with submissions based on user role:
     * - STUDENT: returns assignment details + their own submission (if exists)
     * - TEACHER: returns assignment details + all submissions
     */
    public GetAssignmentDTO getAssignmentById(String assignmentId){
        Assignment assignment = this.assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOTFOUND, assignmentId));
        
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        
        GetAssignmentDTO dto = GetAssignmentDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .courseId(assignment.getCourseId())
                .submissionDate(assignment.getSubmissionDate())
                .createBy(assignment.getCreateBy())
                .createdDate(assignment.getCreatedDate())
                .lastModifiedDate(assignment.getLastModifiedDate())
                .build();

        if (currentUser != null) {
            if ("TEACHER".equals(currentUser.getRole())) {
                // Teacher gets all submissions
                List<SubmissionResponse> allSubmissions = submissionService.getAllSubmissionsByAssignmentId(assignmentId);
                dto.setSubmissions(allSubmissions);
            } else if ("STUDENT".equals(currentUser.getRole())) {
                // Student gets only their own submission
                Optional<SubmissionResponse> mySubmission = submissionService
                        .getStudentSubmissionForAssignment(assignmentId, currentUser.getUserId());
                mySubmission.ifPresent(dto::setMySubmission);
            }
        }

        return dto;
    }

    public List<AssignmentResponse> getAllAssignmentByCourseId(String courseId){
        List<AssignmentResponse> assignmentsResponseList = this.assignmentRepository.findAllByCourseId(courseId)
                .stream()
                .map(assignment -> AssignmentResponse.builder()
                        .id(assignment.getId())
                        .courseId(assignment.getCourseId())
                        .title(assignment.getTitle())
                        .createBy(assignment.getCreateBy())
                        .build()
                )
                .toList();
        if (assignmentsResponseList.isEmpty()) throw new BusinessException(ErrorCode.ASSIGNMENT_NOTFOUND, courseId);

        return assignmentsResponseList;
    }

    @Transactional
    public void deleteAssignmentById(String assignmentId){
        // First delete all submissions for this assignment
        submissionService.deleteAllSubmissionsByAssignmentId(assignmentId);
        // Then delete the assignment
        this.assignmentRepository.deleteById(assignmentId);
    }

    public void updateAssignment(String assignmentId, UpdateAssignmentRequestDTO requestDTO){
        Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOTFOUND, assignmentId));
        this.assignmentMapper.mergeAssignment(assignment, requestDTO);
        this.assignmentRepository.save(assignment);
    }

    private void checkAssignmentTitle(String title) {
        final boolean exist = this.assignmentRepository.existsByTitleIgnoreCase(title);
        if (exist) throw new BusinessException(ErrorCode.ASSIGNMENT_ALREADY_EXISTS, title);
    }

}
