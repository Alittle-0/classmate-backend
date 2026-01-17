package com.devteam.gradingservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "SUBMISSION")
@EntityListeners(AuditingEntityListener.class)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ASSIGNMENT_ID", nullable = false)
    private String assignmentId;

    @Column(name = "STUDENT_ID", nullable = false)
    private String studentId;

    @Column(name = "FIRSTNAME")
    private String firstname;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    @Column(name = "SUBMITTED_DATE", nullable = false)
    private LocalDateTime submittedDate;

    @Column(name = "STATUS")
    private String status; // SUBMITTED, LATE, GRADED

    @Column(name = "GRADE")
    private Double grade;

    @Column(name = "FEEDBACK")
    private String feedback;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE", insertable = false)
    private LocalDateTime lastModifiedDate;
}
