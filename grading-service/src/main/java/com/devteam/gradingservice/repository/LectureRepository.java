package com.devteam.gradingservice.repository;

import com.devteam.gradingservice.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, String> {
    
    List<Lecture> findAllByCourseId(String courseId);
    
    Optional<Lecture> findByIdAndCourseId(String id, String courseId);
    
    boolean existsByTitleIgnoreCaseAndCourseId(String title, String courseId);
}
