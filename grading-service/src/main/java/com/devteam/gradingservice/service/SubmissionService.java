package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.response.SubmissionResponse;
import com.devteam.gradingservice.exception.BusinessException;
import com.devteam.gradingservice.exception.ErrorCode;
import com.devteam.gradingservice.model.Assignment;
import com.devteam.gradingservice.model.Submission;
import com.devteam.gradingservice.repository.AssignmentRepository;
import com.devteam.gradingservice.repository.SubmissionRepository;
import com.devteam.gradingservice.security.SecurityUtils;
import com.devteam.gradingservice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public SubmissionResponse submitAssignment(String assignmentId, String studentId, MultipartFile file) {
        // Get current user to extract firstname and lastname
        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        
        // Check if assignment exists
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOTFOUND, assignmentId));

        // Check submission deadline
        LocalDateTime now = LocalDateTime.now();
        String status = "SUBMITTED";
        if (now.isAfter(assignment.getSubmissionDate())) {
            status = "LATE";
            log.warn("Late submission for assignment: {} by student: {}", assignmentId, studentId);
        }

        // Check if student already submitted
        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId)) {
            // Update existing submission
            Submission existingSubmission = submissionRepository
                    .findByAssignmentIdAndStudentId(assignmentId, studentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SUBMISSION_NOTFOUND, assignmentId));

            // Delete old file
            fileStorageService.deleteSubmissionFile(existingSubmission.getFilePath());

            // Store new file
            String filePath = fileStorageService.storeSubmissionFile(file, assignmentId, studentId);

            existingSubmission.setFileName(file.getOriginalFilename());
            existingSubmission.setFilePath(filePath);
            existingSubmission.setFileSize(file.getSize());
            existingSubmission.setContentType(file.getContentType());
            existingSubmission.setSubmittedDate(now);
            existingSubmission.setStatus(status);
            existingSubmission.setFirstname(currentUser.getFirstname());
            existingSubmission.setLastname(currentUser.getLastname());

            Submission updated = submissionRepository.save(existingSubmission);
            log.info("Submission updated for assignment: {} by student: {}", assignmentId, studentId);
            return mapToResponse(updated);
        }

        // Store file
        String filePath = fileStorageService.storeSubmissionFile(file, assignmentId, studentId);

        // Create submission entity
        Submission submission = Submission.builder()
                .assignmentId(assignmentId)
                .studentId(studentId)
                .firstname(currentUser.getFirstname())
                .lastname(currentUser.getLastname())
                .fileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .submittedDate(now)
                .status(status)
                .build();

        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Submission created for assignment: {} by student: {}", assignmentId, studentId);

        return mapToResponse(savedSubmission);
    }

    public SubmissionResponse getSubmissionById(String submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBMISSION_NOTFOUND, submissionId));
        return mapToResponse(submission);
    }

    public List<SubmissionResponse> getAllSubmissionsByAssignmentId(String assignmentId) {
        List<Submission> submissions = submissionRepository.findAllByAssignmentId(assignmentId);
        return submissions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<SubmissionResponse> getStudentSubmissionForAssignment(String assignmentId, String studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(this::mapToResponse);
    }

    public Resource downloadSubmission(String submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBMISSION_NOTFOUND, submissionId));
        
        return fileStorageService.loadSubmissionFileAsResource(submission.getFilePath());
    }

    /**
     * Delete submission with role-based access control
     * - STUDENT: can only delete their own submission
     * - TEACHER: can delete any submission
     */
    @Transactional
    public void deleteSubmission(String submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBMISSION_NOTFOUND, submissionId));

        UserPrincipal currentUser = SecurityUtils.getCurrentUser();
        
        // Check permission: STUDENT can only delete their own submission
        if ("STUDENT".equals(currentUser.getRole())) {
            if (!submission.getStudentId().equals(currentUser.getUserId())) {
                throw new BusinessException(ErrorCode.SUBMISSION_ACCESS_DENIED, submissionId);
            }
        }
        // TEACHER can delete any submission - no additional check needed

        // Delete file from storage
        fileStorageService.deleteSubmissionFile(submission.getFilePath());

        // Delete submission from database
        submissionRepository.deleteById(submissionId);
        log.info("Submission deleted successfully: {} by user: {}", submissionId, currentUser.getUserId());
    }

    /**
     * Delete all submissions for an assignment (used when deleting assignment)
     */
    @Transactional
    public void deleteAllSubmissionsByAssignmentId(String assignmentId) {
        List<Submission> submissions = submissionRepository.findAllByAssignmentId(assignmentId);
        for (Submission submission : submissions) {
            fileStorageService.deleteSubmissionFile(submission.getFilePath());
            submissionRepository.deleteById(submission.getId());
        }
        log.info("All submissions deleted for assignment: {}", assignmentId);
    }

    public SubmissionResponse mapToResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .assignmentId(submission.getAssignmentId())
                .studentId(submission.getStudentId())
                .firstname(submission.getFirstname())
                .lastname(submission.getLastname())
                .fileName(submission.getFileName())
                .fileSize(submission.getFileSize())
                .contentType(submission.getContentType())
                .submittedDate(submission.getSubmittedDate())
                .status(submission.getStatus())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .createdDate(submission.getCreatedDate())
                .lastModifiedDate(submission.getLastModifiedDate())
                .build();
    }
}
