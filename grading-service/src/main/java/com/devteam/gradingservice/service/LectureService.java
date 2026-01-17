package com.devteam.gradingservice.service;

import com.devteam.gradingservice.dto.request.CreateLectureDTO;
import com.devteam.gradingservice.dto.request.UpdateLectureDTO;
import com.devteam.gradingservice.dto.response.LectureFileResponse;
import com.devteam.gradingservice.dto.response.LectureResponse;
import com.devteam.gradingservice.dto.response.LectureSummaryResponse;
import com.devteam.gradingservice.exception.BusinessException;
import com.devteam.gradingservice.exception.ErrorCode;
import com.devteam.gradingservice.model.Lecture;
import com.devteam.gradingservice.model.LectureFile;
import com.devteam.gradingservice.repository.LectureFileRepository;
import com.devteam.gradingservice.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureFileRepository lectureFileRepository;
    private final FileStorageService fileStorageService;

    /**
     * Create lecture with multiple files
     */
    @Transactional
    public LectureResponse createLecture(CreateLectureDTO request, List<MultipartFile> files) {
        // Check if lecture title already exists in course
        if (lectureRepository.existsByTitleIgnoreCaseAndCourseId(request.getTitle(), request.getCourseId())) {
            throw new BusinessException(ErrorCode.LECTURE_ALREADY_EXISTS, request.getTitle());
        }

        // Create lecture entity
        Lecture lecture = Lecture.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .courseId(request.getCourseId())
                .files(new ArrayList<>())
                .build();

        // Store and add files
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String filePath = fileStorageService.storeLectureFile(file, request.getCourseId());
                    
                    LectureFile lectureFile = LectureFile.builder()
                            .fileName(file.getOriginalFilename())
                            .filePath(filePath)
                            .fileSize(file.getSize())
                            .contentType(file.getContentType())
                            .build();
                    
                    lecture.addFile(lectureFile);
                }
            }
        }

        Lecture savedLecture = lectureRepository.save(lecture);
        log.info("Lecture created successfully with {} files: {}", 
                savedLecture.getFiles().size(), savedLecture.getId());

        return mapToResponse(savedLecture);
    }

    public LectureResponse getLectureById(String lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND, lectureId));
        return mapToResponse(lecture);
    }

    public List<LectureSummaryResponse> getAllLecturesByCourseId(String courseId) {
        List<Lecture> lectures = lectureRepository.findAllByCourseId(courseId);
        return lectures.stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    /**
     * Download a specific file from a lecture
     */
    public Resource downloadLectureFile(String fileId) {
        LectureFile lectureFile = lectureFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_FILE_NOT_FOUND, fileId));
        
        return fileStorageService.loadLectureFileAsResource(lectureFile.getFilePath());
    }

    /**
     * Get lecture file info by ID
     */
    public LectureFileResponse getLectureFileById(String fileId) {
        LectureFile lectureFile = lectureFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_FILE_NOT_FOUND, fileId));
        return mapToFileResponse(lectureFile);
    }

    /**
     * Update lecture info (title, description)
     */
    @Transactional
    public LectureResponse updateLectureInfo(String lectureId, UpdateLectureDTO request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND, lectureId));

        // Update title if provided
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            // Check if new title already exists in the same course (excluding current lecture)
            if (!lecture.getTitle().equalsIgnoreCase(request.getTitle()) &&
                lectureRepository.existsByTitleIgnoreCaseAndCourseId(request.getTitle(), lecture.getCourseId())) {
                throw new BusinessException(ErrorCode.LECTURE_ALREADY_EXISTS, request.getTitle());
            }
            lecture.setTitle(request.getTitle());
        }
        
        // Update description if provided
        if (request.getDescription() != null) {
            lecture.setDescription(request.getDescription());
        }

        Lecture updatedLecture = lectureRepository.save(lecture);
        log.info("Lecture info updated successfully: {}", lectureId);

        return mapToResponse(updatedLecture);
    }

    /**
     * Add files to existing lecture
     */
    @Transactional
    public LectureResponse addFilesToLecture(String lectureId, List<MultipartFile> files) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND, lectureId));

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String filePath = fileStorageService.storeLectureFile(file, lecture.getCourseId());
                    
                    LectureFile lectureFile = LectureFile.builder()
                            .fileName(file.getOriginalFilename())
                            .filePath(filePath)
                            .fileSize(file.getSize())
                            .contentType(file.getContentType())
                            .build();
                    
                    lecture.addFile(lectureFile);
                }
            }
        }

        Lecture updatedLecture = lectureRepository.save(lecture);
        log.info("Added {} files to lecture: {}", files != null ? files.size() : 0, lectureId);

        return mapToResponse(updatedLecture);
    }

    /**
     * Delete a specific file from lecture
     */
    @Transactional
    public LectureResponse deleteFileFromLecture(String lectureId, String fileId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND, lectureId));

        LectureFile fileToDelete = lecture.getFiles().stream()
                .filter(f -> f.getId().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_FILE_NOT_FOUND, fileId));

        // Delete file from storage
        fileStorageService.deleteLectureFile(fileToDelete.getFilePath());

        // Remove from lecture
        lecture.removeFile(fileToDelete);
        
        Lecture updatedLecture = lectureRepository.save(lecture);
        log.info("File {} deleted from lecture: {}", fileId, lectureId);

        return mapToResponse(updatedLecture);
    }

    /**
     * Delete entire lecture with all files
     */
    @Transactional
    public void deleteLecture(String lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND, lectureId));

        // Delete all files from storage
        for (LectureFile file : lecture.getFiles()) {
            fileStorageService.deleteLectureFile(file.getFilePath());
        }

        // Delete lecture from database (cascade will delete LectureFile entities)
        lectureRepository.deleteById(lectureId);
        log.info("Lecture deleted successfully: {}", lectureId);
    }

    private LectureResponse mapToResponse(Lecture lecture) {
        List<LectureFileResponse> fileResponses = lecture.getFiles().stream()
                .map(this::mapToFileResponse)
                .toList();

        long totalSize = lecture.getFiles().stream()
                .mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0)
                .sum();

        return LectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .courseId(lecture.getCourseId())
                .uploadedBy(lecture.getUploadedBy())
                .createdDate(lecture.getCreatedDate())
                .lastModifiedDate(lecture.getLastModifiedDate())
                .files(fileResponses)
                .totalFileSize(totalSize)
                .fileCount(fileResponses.size())
                .build();
    }

    private LectureFileResponse mapToFileResponse(LectureFile file) {
        return LectureFileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .contentType(file.getContentType())
                .createdDate(file.getCreatedDate())
                .build();
    }

    private LectureSummaryResponse mapToSummaryResponse(Lecture lecture) {
        return LectureSummaryResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .build();
    }
}
