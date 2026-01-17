package com.devteam.gradingservice.controller;

import com.devteam.gradingservice.dto.request.CreateLectureDTO;
import com.devteam.gradingservice.dto.request.UpdateLectureDTO;
import com.devteam.gradingservice.dto.response.LectureFileResponse;
import com.devteam.gradingservice.dto.response.LectureResponse;
import com.devteam.gradingservice.dto.response.LectureSummaryResponse;
import com.devteam.gradingservice.service.LectureService;
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
@RequestMapping("/api/v1/grading/lectures")
@RequiredArgsConstructor
@Slf4j
public class LectureController {

    private final LectureService lectureService;

    /**
     * Create lecture with multiple files (TEACHER only)
     */
    @PostMapping(value = "/course/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LectureResponse> createLecture(
            @PathVariable String courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        
        log.info("Creating lecture: {} for course: {} with {} files", 
                title, courseId, files != null ? files.size() : 0);
        
        CreateLectureDTO request = CreateLectureDTO.builder()
                .title(title)
                .courseId(courseId)
                .description(description)
                .build();
        
        LectureResponse response = lectureService.createLecture(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get lecture by ID (returns lecture info with all files)
     */
    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureResponse> getLectureById(@PathVariable String lectureId) {
        log.info("Getting lecture: {}", lectureId);
        LectureResponse lecture = lectureService.getLectureById(lectureId);
        return ResponseEntity.ok(lecture);
    }

    /**
     * Get all lectures by course ID (returns only id and title)
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LectureSummaryResponse>> getAllLecturesByCourseId(@PathVariable String courseId) {
        log.info("Getting all lectures for course: {}", courseId);
        List<LectureSummaryResponse> lectures = lectureService.getAllLecturesByCourseId(courseId);
        return ResponseEntity.ok(lectures);
    }

    /**
     * Download a specific file from lecture
     */
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadLectureFile(@PathVariable String fileId) {
        log.info("Downloading lecture file: {}", fileId);
        
        LectureFileResponse fileInfo = lectureService.getLectureFileById(fileId);
        Resource resource = lectureService.downloadLectureFile(fileId);
        
        String contentType = fileInfo.getContentType() != null ? 
                fileInfo.getContentType() : "application/octet-stream";
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Update lecture info - title and/or description (TEACHER only)
     */
    @PatchMapping("/{lectureId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LectureResponse> updateLectureInfo(
            @PathVariable String lectureId,
            @RequestBody UpdateLectureDTO request) {
        log.info("Updating lecture info: {}", lectureId);
        LectureResponse response = lectureService.updateLectureInfo(lectureId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Add files to existing lecture (TEACHER only)
     */
    @PostMapping(value = "/{lectureId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LectureResponse> addFilesToLecture(
            @PathVariable String lectureId,
            @RequestParam("files") List<MultipartFile> files) {
        log.info("Adding {} files to lecture: {}", files.size(), lectureId);
        LectureResponse response = lectureService.addFilesToLecture(lectureId, files);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific file from lecture (TEACHER only)
     */
    @DeleteMapping("/{lectureId}/files/{fileId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<LectureResponse> deleteFileFromLecture(
            @PathVariable String lectureId,
            @PathVariable String fileId) {
        log.info("Deleting file {} from lecture: {}", fileId, lectureId);
        LectureResponse response = lectureService.deleteFileFromLecture(lectureId, fileId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete entire lecture with all files (TEACHER only)
     */
    @DeleteMapping("/{lectureId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteLecture(@PathVariable String lectureId) {
        log.info("Deleting lecture: {}", lectureId);
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.noContent().build();
    }
}
