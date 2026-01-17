package com.devteam.gradingservice.service;

import com.devteam.gradingservice.exception.FileStorageErrorCode;
import com.devteam.gradingservice.exception.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path lectureStorageLocation;
    private final Path submissionStorageLocation;

    @Value("${file.max-size:10485760}") // Default 10MB
    private long maxFileSize;

    // Allowed file types
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".pdf",   // PDF documents
            ".ppt", ".pptx",  // PowerPoint presentations
            ".doc", ".docx",  // Word documents
            ".xls", ".xlsx",  // Excel spreadsheets
            ".zip",   // ZIP archives
            ".rar"    // RAR archives
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            // PDF
            "application/pdf",
            // PowerPoint
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            // Word
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            // Excel
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            // ZIP
            "application/zip",
            "application/x-zip-compressed",
            // RAR
            "application/x-rar-compressed",
            "application/vnd.rar",
            "application/octet-stream"  // Some browsers send this for RAR
    );

    public FileStorageService(
            @Value("${file.upload-dir:uploads}") String uploadDir,
            @Value("${file.lecture-dir:lectures}") String lectureDir,
            @Value("${file.submission-dir:submissions}") String submissionDir) {
        
        this.lectureStorageLocation = Paths.get(uploadDir, lectureDir).toAbsolutePath().normalize();
        this.submissionStorageLocation = Paths.get(uploadDir, submissionDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.lectureStorageLocation);
            Files.createDirectories(this.submissionStorageLocation);
            log.info("Created upload directories - Lectures: {}, Submissions: {}", 
                    lectureStorageLocation, submissionStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException(FileStorageErrorCode.FILE_STORAGE_EXCEPTION);
        }
    }

    /**
     * Store lecture file
     */
    public String storeLectureFile(MultipartFile file, String courseId) {
        return storeFile(file, lectureStorageLocation, courseId);
    }

    /**
     * Store submission file
     */
    public String storeSubmissionFile(MultipartFile file, String assignmentId, String studentId) {
        String subDirectory = assignmentId + "/" + studentId;
        Path targetLocation = submissionStorageLocation.resolve(subDirectory);
        
        try {
            Files.createDirectories(targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException(FileStorageErrorCode.FILE_STORAGE_EXCEPTION);
        }
        
        // Pass subDirectory so the returned path is relative to submissionStorageLocation
        return storeFile(file, submissionStorageLocation, subDirectory);
    }

    /**
     * Common store file method
     */
    private String storeFile(MultipartFile file, Path storageLocation, String subDirectory) {
        // Validate file
        validateFile(file);

        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException(FileStorageErrorCode.INVALID_FILE_PATH, originalFileName);
            }

            // Generate unique filename
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to the target location
            Path targetLocation = storageLocation;
            if (subDirectory != null && !subDirectory.isEmpty()) {
                targetLocation = storageLocation.resolve(subDirectory);
                Files.createDirectories(targetLocation);
            }
            
            Path destinationFile = targetLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", destinationFile);
            
            // Return relative path from storage location
            return storageLocation.relativize(destinationFile).toString();

        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFileName, ex);
            throw new FileStorageException(FileStorageErrorCode.FILE_STORAGE_EXCEPTION);
        }
    }

    /**
     * Load lecture file as Resource
     */
    public Resource loadLectureFileAsResource(String fileName) {
        return loadFileAsResource(fileName, lectureStorageLocation);
    }

    /**
     * Load submission file as Resource
     */
    public Resource loadSubmissionFileAsResource(String fileName) {
        return loadFileAsResource(fileName, submissionStorageLocation);
    }

    /**
     * Common load file method
     */
    private Resource loadFileAsResource(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException(FileStorageErrorCode.FILE_NOT_FOUND, fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not found: {}", fileName, ex);
            throw new FileStorageException(FileStorageErrorCode.FILE_NOT_FOUND, fileName);
        }
    }

    /**
     * Delete lecture file
     */
    public void deleteLectureFile(String fileName) {
        deleteFile(fileName, lectureStorageLocation);
    }

    /**
     * Delete submission file
     */
    public void deleteSubmissionFile(String fileName) {
        deleteFile(fileName, submissionStorageLocation);
    }

    /**
     * Common delete file method
     */
    private void deleteFile(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", filePath);
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", fileName, ex);
            throw new FileStorageException(FileStorageErrorCode.FILE_STORAGE_EXCEPTION);
        }
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new FileStorageException(FileStorageErrorCode.FILE_STORAGE_EXCEPTION);
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new FileStorageException(FileStorageErrorCode.FILE_TOO_LARGE, maxFileSize / (1024 * 1024));
        }

        // Validate file type by extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new FileStorageException(FileStorageErrorCode.INVALID_FILE_TYPE, "unknown");
        }

        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new FileStorageException(
                    FileStorageErrorCode.INVALID_FILE_TYPE,
                    fileExtension.isEmpty() ? "no extension" : fileExtension
            );
        }

        // Validate file type by content type
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("File uploaded with content type: {} for extension: {}", contentType, fileExtension);
            // Don't throw error for content type mismatch, just log warning
            // Some browsers send different content types for the same file
        }

        log.debug("File validation passed - Name: {}, Extension: {}, ContentType: {}, Size: {} bytes",
                originalFileName, fileExtension, contentType, file.getSize());
    }

    /**
     * Get file size
     */
    public long getFileSize(String fileName, Path storageLocation) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            return Files.size(filePath);
        } catch (IOException ex) {
            return 0;
        }
    }
}
