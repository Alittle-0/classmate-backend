package com.devteam.gradingservice.controller;

import com.devteam.gradingservice.dto.request.CreateAssignmentDTO;
import com.devteam.gradingservice.dto.request.UpdateAssignmentRequestDTO;
import com.devteam.gradingservice.dto.response.AssignmentResponse;
import com.devteam.gradingservice.dto.response.GetAssignmentDTO;
import com.devteam.gradingservice.dto.response.SubmissionResponse;
import com.devteam.gradingservice.security.SecurityUtils;
import com.devteam.gradingservice.service.AssignmentService;
import com.devteam.gradingservice.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grading/assignments")
@RequiredArgsConstructor
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    /**
     * Create new assignment (TEACHER only)
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> createAssignment(@Valid @RequestBody CreateAssignmentDTO request) {
        log.info("Creating assignment: {}", request.getTitle());
        this.assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update assignment (TEACHER only)
     */
    @PatchMapping("/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> updateAssignment(
            @PathVariable String assignmentId,
            @Valid @RequestBody UpdateAssignmentRequestDTO request) {
        log.info("Updating assignment: {}", assignmentId);
        assignmentService.updateAssignment(assignmentId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Get assignment full details
     * - STUDENT: returns assignment + their own submission (if exists)
     * - TEACHER: returns assignment + all submissions
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<GetAssignmentDTO> getAssignmentFullDetails(@PathVariable String assignmentId) {
        log.info("Getting full details for assignment: {}", assignmentId);
        return ResponseEntity.ok(this.assignmentService.getAssignmentById(assignmentId));
    }

    /**
     * Get all assignments by course ID
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssignmentResponse>> getAllAssignmentsByCourseId(@PathVariable String courseId) {
        log.info("Getting all assignments for course: {}", courseId);
        return ResponseEntity.ok(this.assignmentService.getAllAssignmentByCourseId(courseId));
    }

    /**
     * Delete assignment (TEACHER only)
     * Also deletes all submissions for this assignment
     */
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String assignmentId) {
        log.info("Deleting assignment: {}", assignmentId);
        assignmentService.deleteAssignmentById(assignmentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== SUBMISSION APIs ====================

    /**
     * Submit file for assignment (STUDENT only)
     * If already submitted, updates the existing submission
     */
    @PostMapping(value = "/{assignmentId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionResponse> submitAssignment(
            @PathVariable String assignmentId,
            @RequestParam("file") MultipartFile file) {
        
        String studentId = SecurityUtils.getCurrentUser().getUserId();
        log.info("Student {} submitting assignment: {}", studentId, assignmentId);
        
        SubmissionResponse response = submissionService.submitAssignment(assignmentId, studentId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Download submission file
     */
    @GetMapping("/submissions/{submissionId}/download")
    public ResponseEntity<Resource> downloadSubmission(@PathVariable String submissionId) {
        log.info("Downloading submission: {}", submissionId);
        
        SubmissionResponse submission = submissionService.getSubmissionById(submissionId);
        Resource resource = submissionService.downloadSubmission(submissionId);
        
        String contentType = submission.getContentType() != null ? 
                submission.getContentType() : "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + submission.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Delete submission
     * - STUDENT: can only delete their own submission
     * - TEACHER: can delete any submission
     */
    @DeleteMapping("/submissions/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String submissionId) {
        log.info("Deleting submission: {}", submissionId);
        submissionService.deleteSubmission(submissionId);
        return ResponseEntity.noContent().build();
    }
}
