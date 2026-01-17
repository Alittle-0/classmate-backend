# 📚 Grading Service API Documentation

## Overview

Grading Service quản lý assignments, lectures, và submissions cho Student Management System.

**Base URL:** `http://localhost:8080/api/v1/grading`  
**Service Port:** 8083  
**Database:** PostgreSQL (school_grading_db)

---

## 🔐 Security

- **Authentication:** JWT token validated by API Gateway
- **Authorization:** Role-based access control
  - **TEACHER:** Can create, update, delete assignments and lectures
  - **STUDENT:** Can submit assignments and view their submissions
  - **ALL:** Can view assignments and lectures

---

## 📝 Assignment APIs

### 1. Create Assignment (TEACHER Only)

**Endpoint:** `POST /api/v1/grading/assignments/create`

**Request Body:**

```json
{
  "title": "Assignment 1",
  "description": "Complete the exercises",
  "courseId": "course-uuid",
  "createBy": "teacher-uuid",
  "submissionDate": "2026-01-31 23:59:59"
}
```

**Response:** `201 Created`

**Authorization:** `TEACHER`

---

### 2. Update Assignment (TEACHER Only)

**Endpoint:** `PATCH /api/v1/grading/assignments/{assignmentId}`

**Request Body:**

```json
{
  "title": "Updated Assignment Title",
  "description": "Updated description",
  "submissionDate": "2026-02-15 23:59:59"
}
```

**Response:** `200 OK`

**Authorization:** `TEACHER`

---

### 3. Get Assignment Basic Info

**Endpoint:** `GET /api/v1/grading/assignments/{assignmentId}/basic`

**Response:**

```json
{
  "id": "assignment-uuid",
  "title": "Assignment 1",
  "courseId": "course-uuid",
  "createBy": "teacher-uuid"
}
```

**Authorization:** `ANY`

---

### 4. Get Assignment Full Details

**Endpoint:** `GET /api/v1/grading/assignments/{assignmentId}`

**Response:**

```json
{
  "id": "assignment-uuid",
  "title": "Assignment 1",
  "description": "Complete the exercises",
  "courseId": "course-uuid",
  "submissionDate": "2026-01-31T23:59:59",
  "createBy": "teacher-uuid",
  "createdDate": "2026-01-14T10:00:00",
  "lastModifiedDate": "2026-01-14T11:00:00"
}
```

**Authorization:** `ANY`

---

### 5. Get All Assignments by Course ID

**Endpoint:** `GET /api/v1/grading/assignments/course/{courseId}`

**Response:**

```json
[
  {
    "id": "assignment-uuid-1",
    "title": "Assignment 1",
    "courseId": "course-uuid",
    "createBy": "teacher-uuid"
  },
  {
    "id": "assignment-uuid-2",
    "title": "Assignment 2",
    "courseId": "course-uuid",
    "createBy": "teacher-uuid"
  }
]
```

**Authorization:** `ANY`

---

### 6. Delete Assignment (TEACHER Only)

**Endpoint:** `DELETE /api/v1/grading/assignments/{assignmentId}`

**Response:** `204 No Content`

**Authorization:** `TEACHER`

---

## 📖 Lecture APIs

### 1. Upload Lecture (TEACHER Only)

**Endpoint:** `POST /api/v1/grading/lectures/upload`

**Content-Type:** `multipart/form-data`

**Form Data:**

- `title` (required): Lecture title
- `courseId` (required): Course UUID
- `description` (optional): Lecture description
- `file` (required): File to upload (Max 10MB)

**Response:** `201 Created`

```json
{
  "id": "lecture-uuid",
  "title": "Lecture 1: Introduction",
  "description": "Introduction to the course",
  "courseId": "course-uuid",
  "fileName": "lecture1.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadedBy": "teacher-uuid",
  "createdDate": "2026-01-14T10:00:00",
  "lastModifiedDate": null
}
```

**Authorization:** `TEACHER`

---

### 2. Get Lecture by ID

**Endpoint:** `GET /api/v1/grading/lectures/{lectureId}`

**Response:**

```json
{
  "id": "lecture-uuid",
  "title": "Lecture 1: Introduction",
  "description": "Introduction to the course",
  "courseId": "course-uuid",
  "fileName": "lecture1.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadedBy": "teacher-uuid",
  "createdDate": "2026-01-14T10:00:00",
  "lastModifiedDate": null
}
```

**Authorization:** `ANY`

---

### 3. Get All Lectures by Course ID

**Endpoint:** `GET /api/v1/grading/lectures/course/{courseId}`

**Response:**

```json
[
  {
    "id": "lecture-uuid-1",
    "title": "Lecture 1: Introduction",
    "courseId": "course-uuid",
    "fileName": "lecture1.pdf",
    "fileSize": 1024000,
    "uploadedBy": "teacher-uuid",
    "createdDate": "2026-01-14T10:00:00"
  },
  {
    "id": "lecture-uuid-2",
    "title": "Lecture 2: Advanced Topics",
    "courseId": "course-uuid",
    "fileName": "lecture2.pdf",
    "fileSize": 2048000,
    "uploadedBy": "teacher-uuid",
    "createdDate": "2026-01-15T10:00:00"
  }
]
```

**Authorization:** `ANY`

---

### 4. Download Lecture File

**Endpoint:** `GET /api/v1/grading/lectures/{lectureId}/download`

**Response:** File download

**Headers:**

- `Content-Type`: File content type
- `Content-Disposition`: `attachment; filename="lecture1.pdf"`

**Authorization:** `ANY`

---

### 5. Update Lecture (TEACHER Only)

**Endpoint:** `PATCH /api/v1/grading/lectures/{lectureId}`

**Request Body:**

```json
{
  "title": "Updated Lecture Title",
  "description": "Updated description"
}
```

**Response:** `200 OK`

```json
{
  "id": "lecture-uuid",
  "title": "Updated Lecture Title",
  "description": "Updated description",
  "courseId": "course-uuid",
  "fileName": "lecture1.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadedBy": "teacher-uuid",
  "createdDate": "2026-01-14T10:00:00",
  "lastModifiedDate": "2026-01-14T15:00:00"
}
```

**Authorization:** `TEACHER`

---

### 6. Delete Lecture (TEACHER Only)

**Endpoint:** `DELETE /api/v1/grading/lectures/{lectureId}`

**Response:** `204 No Content`

**Note:** This will delete both the database record and the file from storage.

**Authorization:** `TEACHER`

---

## 📤 Submission APIs

### 1. Submit Assignment (STUDENT Only)

**Endpoint:** `POST /api/v1/grading/submissions/{assignmentId}/submit`

**Content-Type:** `multipart/form-data`

**Form Data:**

- `file` (required): Assignment file to upload (Max 10MB)

**Response:** `201 Created`

```json
{
  "id": "submission-uuid",
  "assignmentId": "assignment-uuid",
  "studentId": "student-uuid",
  "fileName": "assignment1.pdf",
  "fileSize": 512000,
  "contentType": "application/pdf",
  "submittedDate": "2026-01-20T14:30:00",
  "status": "SUBMITTED",
  "grade": null,
  "feedback": null,
  "createdDate": "2026-01-20T14:30:00"
}
```

**Status Values:**

- `SUBMITTED`: Submitted on time
- `LATE`: Submitted after deadline

**Notes:**

- Automatically checks submission deadline
- If resubmitting, previous file will be replaced
- Student ID is extracted from JWT token

**Authorization:** `STUDENT`

---

### 2. Get Submission by ID

**Endpoint:** `GET /api/v1/grading/submissions/{submissionId}`

**Response:**

```json
{
  "id": "submission-uuid",
  "assignmentId": "assignment-uuid",
  "studentId": "student-uuid",
  "fileName": "assignment1.pdf",
  "fileSize": 512000,
  "contentType": "application/pdf",
  "submittedDate": "2026-01-20T14:30:00",
  "status": "SUBMITTED",
  "grade": null,
  "feedback": null,
  "createdDate": "2026-01-20T14:30:00"
}
```

**Authorization:** `ANY`

---

### 3. Get All Submissions by Assignment ID (TEACHER Only)

**Endpoint:** `GET /api/v1/grading/submissions/assignment/{assignmentId}`

**Response:**

```json
[
  {
    "id": "submission-uuid-1",
    "assignmentId": "assignment-uuid",
    "studentId": "student-uuid-1",
    "fileName": "assignment1.pdf",
    "submittedDate": "2026-01-20T14:30:00",
    "status": "SUBMITTED"
  },
  {
    "id": "submission-uuid-2",
    "assignmentId": "assignment-uuid",
    "studentId": "student-uuid-2",
    "fileName": "assignment1.docx",
    "submittedDate": "2026-01-25T23:59:00",
    "status": "LATE"
  }
]
```

**Authorization:** `TEACHER`

---

### 4. Get My Submissions (STUDENT Only)

**Endpoint:** `GET /api/v1/grading/submissions/my-submissions`

**Response:**

```json
[
  {
    "id": "submission-uuid-1",
    "assignmentId": "assignment-uuid-1",
    "studentId": "student-uuid",
    "fileName": "assignment1.pdf",
    "submittedDate": "2026-01-20T14:30:00",
    "status": "SUBMITTED",
    "grade": 85.5,
    "feedback": "Good work!"
  },
  {
    "id": "submission-uuid-2",
    "assignmentId": "assignment-uuid-2",
    "studentId": "student-uuid",
    "fileName": "assignment2.pdf",
    "submittedDate": "2026-01-25T23:59:00",
    "status": "LATE",
    "grade": null,
    "feedback": null
  }
]
```

**Authorization:** `STUDENT`

---

### 5. Download Submission File

**Endpoint:** `GET /api/v1/grading/submissions/{submissionId}/download`

**Response:** File download

**Headers:**

- `Content-Type`: File content type
- `Content-Disposition`: `attachment; filename="assignment1.pdf"`

**Authorization:** `ANY`

---

### 6. Delete Submission (TEACHER Only)

**Endpoint:** `DELETE /api/v1/grading/submissions/{submissionId}`

**Response:** `204 No Content`

**Note:** This will delete both the database record and the file from storage.

**Authorization:** `TEACHER`

---

## 📊 Data Models

### Assignment Model

```java
{
  id: String (UUID)
  title: String
  description: String
  courseId: String
  submissionDate: LocalDateTime
  createBy: String
  lastModifiedBy: String
  createdDate: LocalDateTime
  lastModifiedDate: LocalDateTime
}
```

### Lecture Model

```java
{
  id: String (UUID)
  title: String
  description: String
  courseId: String
  fileName: String
  filePath: String
  fileSize: Long
  contentType: String
  uploadedBy: String
  lastModifiedBy: String
  createdDate: LocalDateTime
  lastModifiedDate: LocalDateTime
}
```

### Submission Model

```java
{
  id: String (UUID)
  assignmentId: String
  studentId: String
  fileName: String
  filePath: String
  fileSize: Long
  contentType: String
  submittedDate: LocalDateTime
  status: String (SUBMITTED, LATE, GRADED)
  grade: Double
  feedback: String
  createdDate: LocalDateTime
}
```

---

## ⚠️ Error Codes

| Code                        | Message                                             | Status |
| --------------------------- | --------------------------------------------------- | ------ |
| `ASSIGNMENT_NOT_FOUND`      | Assignment not found with id %s                     | 404    |
| `ASSIGNMENT_ALREADY_EXISTS` | Assignment with title %s already exists             | 400    |
| `LECTURE_NOT_FOUND`         | Lecture not found with id %s                        | 404    |
| `LECTURE_ALREADY_EXISTS`    | Lecture with title %s already exists in this course | 400    |
| `SUBMISSION_NOT_FOUND`      | Submission not found with id %s                     | 404    |
| `SUBMISSION_ALREADY_EXISTS` | Submission already exists for this assignment       | 400    |
| `FILE_STORAGE_EXCEPTION`    | Could not store file. Please try again!             | 500    |
| `FILE_NOT_FOUND`            | File not found: %s                                  | 404    |
| `FILE_TOO_LARGE`            | File size exceeds maximum limit of %s MB            | 400    |
| `INVALID_FILE_TYPE`         | File type not allowed: %s                           | 400    |

---

## 🔧 Configuration

### File Upload Settings

```properties
# Maximum file size: 10MB
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Upload directories
file.upload-dir=uploads
file.lecture-dir=lectures
file.submission-dir=submissions
file.max-size=10485760 # 10MB in bytes
```

### File Storage Structure

```
uploads/
├── lectures/
│   ├── course-uuid/
│   │   ├── generated-uuid.pdf
│   │   └── generated-uuid.pptx
└── submissions/
    ├── assignment-uuid/
    │   ├── student-uuid/
    │   │   └── generated-uuid.pdf
```

---

## 🚀 Features

### ✅ Implemented

- ✅ Assignment CRUD operations
- ✅ Lecture upload, download, update, delete
- ✅ Student submission with deadline checking
- ✅ Automatic file storage management
- ✅ Role-based authorization
- ✅ File size validation (10MB max)
- ✅ Resubmission support (replaces old file)
- ✅ Automatic status detection (SUBMITTED/LATE)
- ✅ JPA Auditing (auto createdBy, lastModifiedBy)

### 🔜 Future Enhancements

- Grade assignment functionality
- Feedback system for submissions
- Bulk download submissions
- File type restrictions
- Plagiarism detection
- Assignment templates
- Due date reminders

---

## 📚 Usage Examples

### Example 1: Teacher creates assignment

```http
POST /api/v1/grading/assignments/create
Authorization: Bearer {teacher-jwt}
Content-Type: application/json

{
  "title": "Midterm Exam",
  "description": "Complete all questions",
  "courseId": "course-123",
  "createBy": "teacher-456",
  "submissionDate": "2026-02-15 23:59:59"
}
```

### Example 2: Teacher uploads lecture

```http
POST /api/v1/grading/lectures/upload
Authorization: Bearer {teacher-jwt}
Content-Type: multipart/form-data

title=Introduction to Java
courseId=course-123
description=Chapter 1 lecture
file=@lecture1.pdf
```

### Example 3: Student submits assignment

```http
POST /api/v1/grading/submissions/assignment-123/submit
Authorization: Bearer {student-jwt}
Content-Type: multipart/form-data

file=@my-assignment.pdf
```

### Example 4: Student downloads lecture

```http
GET /api/v1/grading/lectures/lecture-123/download
Authorization: Bearer {student-jwt}
```

---

## 🔐 Security Notes

1. **JWT Validation:** All requests go through API Gateway for JWT validation
2. **User Context:** User ID is automatically extracted from JWT token
3. **Role Enforcement:** @PreAuthorize ensures proper authorization
4. **File Access:** Only authenticated users can upload/download files
5. **Deadline Check:** Automatic deadline validation for submissions

---

## 📞 Contact & Support

For issues or questions, please refer to the main project documentation.

**Service Status:** ✅ Ready for Production  
**Last Updated:** January 14, 2026
