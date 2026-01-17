package com.devteam.gradingservice.repository;

import com.devteam.gradingservice.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    List<Assignment> findAllByCourseId(String courseId);

    boolean existsByTitleIgnoreCase(String title);
}
