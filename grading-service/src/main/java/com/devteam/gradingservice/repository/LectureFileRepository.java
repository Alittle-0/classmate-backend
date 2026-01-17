package com.devteam.gradingservice.repository;

import com.devteam.gradingservice.model.LectureFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureFileRepository extends JpaRepository<LectureFile, String> {
    
    List<LectureFile> findAllByLectureId(String lectureId);
}
