package com.devteam.academicservice.repository;

import com.devteam.academicservice.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findAllById(String id);

    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "members")
    @Query("SELECT c FROM Course c JOIN c.members m WHERE m.memberId = :userId")
    List<Course> findAllByMemberId(@Param("userId") String userId);

    Optional<Course> findByInvitedCode(String inviteCode);

    @EntityGraph(attributePaths = "members")
    Optional<Course> findById(String id);

    @EntityGraph(attributePaths = "members")
    List<Course> findAll();
}
