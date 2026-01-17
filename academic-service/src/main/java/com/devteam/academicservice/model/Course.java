package com.devteam.academicservice.model;

import com.devteam.academicservice.security.RandomCodeGenerator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "COURSES")
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "INVITED_CODE", unique = true, nullable = false, length = 10)
    private String invitedCode;

    @Column(name = "TEACHER_ID", nullable = false, updatable = false)
    private String teacherId;

    @Column(name = "TEACHER_FISTNAME", nullable = false, updatable = false)
    private String teacherFirstname;

    @Column(name = "TEACHER_LASTNAME", nullable = false, updatable = false)
    private String teacherLastname;

    @Column(name = "SUB_TEACHER_ID")
    private String subTeacherId;

    @Column(name = "SUB_TEACHER_NAME")
    private String subTeacherName;

    // @Column(name = "SUB_TEACHER_FISTNAME")
    // private String subTeacherFirstname;

    // @Column(name = "SUB_TEACHER_LASTNAME")
    // private String subTeacherLastname;

   @ManyToMany(
    // fetch = FetchType.EAGER
           cascade = {
                   CascadeType.PERSIST,
                   CascadeType.MERGE
           }
    )
    @JoinTable(
            name = "COURSES_MEMBERS",
            joinColumns = {
                    @JoinColumn(name = "COURSES_ID")
            },
            inverseJoinColumns = @JoinColumn(name = "MEMBERS_ID")
    )
   @JsonManagedReference
   private List<Member> members;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE", insertable = false)
    private LocalDateTime lastModifiedDate;

    @PrePersist
    public void generateInviteCode() {
        if (invitedCode == null) {
            invitedCode = RandomCodeGenerator.generateCode(10);
        }
    }

}