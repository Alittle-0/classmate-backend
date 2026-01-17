package com.devteam.academicservice.controller;

import com.devteam.academicservice.dto.request.CreateCourseDTO;
import com.devteam.academicservice.dto.request.UpdateCourseRequestDTO;
import com.devteam.academicservice.dto.response.CourseDetailsResponseDTO;
import com.devteam.academicservice.dto.response.CourseResponse;
import com.devteam.academicservice.model.Course;
import com.devteam.academicservice.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(this.courseService.getAllCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailsResponseDTO> getCourseById(@PathVariable final String courseId) {
        return ResponseEntity.ok(this.courseService.getCourseById(courseId));
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseResponse>> getAllCoursesById() {
        return ResponseEntity.ok(this.courseService.getAllCoursesById());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCourse(
            @Valid
            @RequestBody
            final CreateCourseDTO course
    ) {
        this.courseService.createCourse(course);
    }

    @PatchMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCourseById(@PathVariable final String courseId, @Valid @RequestBody final UpdateCourseRequestDTO requestDTO) {
        this.courseService.updateCourseById(courseId, requestDTO);
    }

    @PatchMapping("/invite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMember(@RequestParam("code") final String invitedCode) {
        this.courseService.addMember(invitedCode);
    }

    @PatchMapping("/{courseId}/{memberId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable("courseId") final String courseId, @PathVariable("memberId") final String memberId) {
        this.courseService.removeMember(courseId, memberId);
    }

    @PatchMapping("/{courseId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable("courseId") final String courseId) {
        this.courseService.leaveCourse(courseId);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourseById(@PathVariable final String courseId) {
        this.courseService.deleteCourseById(courseId);
    }

}
