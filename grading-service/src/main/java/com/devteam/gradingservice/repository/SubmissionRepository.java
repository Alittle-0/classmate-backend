package com.devteam.gradingservice.repository;

import com.devteam.gradingservice.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    
    List<Submission> findAllByAssignmentId(String assignmentId);
    
    Optional<Submission> findByAssignmentIdAndStudentId(String assignmentId, String studentId);
    
    List<Submission> findAllByStudentId(String studentId);
    
    boolean existsByAssignmentIdAndStudentId(String assignmentId, String studentId);
}
