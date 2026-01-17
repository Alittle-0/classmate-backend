# 🎓 Grading Service

Service quản lý assignments, lectures, và submissions cho Student Management System.

## 📋 Tổng Quan

Grading Service cung cấp các chức năng:

- ✅ **Assignment Management** - Tạo, cập nhật, xóa assignments (TEACHER only)
- ✅ **Lecture Management** - Upload, download, update, delete lectures (TEACHER only)
- ✅ **Submission Management** - Student submit assignments với kiểm tra deadline tự động
- ✅ **File Storage** - Lưu trữ files an toàn với validation
- ✅ **Role-based Authorization** - Phân quyền TEACHER/STUDENT
- ✅ **Deadline Checking** - Tự động đánh dấu LATE submissions

## 🚀 Quick Start

### 1. Cấu Hình Database

```sql
CREATE DATABASE school_grading_db;
```

### 2. Chạy Service

```bash
cd grading-service
mvnw spring-boot:run
```

Service sẽ chạy trên port **8083**.

### 3. Truy Cập qua API Gateway

Base URL: `http://localhost:8080/api/v1/grading`

## 📚 API Endpoints

### Assignment APIs

| Method | Endpoint                         | Role    | Description                       |
| ------ | -------------------------------- | ------- | --------------------------------- |
| POST   | `/assignments/create`            | TEACHER | Tạo assignment mới                |
| PATCH  | `/assignments/{id}`              | TEACHER | Cập nhật assignment               |
| GET    | `/assignments/{id}/basic`        | ANY     | Lấy thông tin cơ bản              |
| GET    | `/assignments/{id}`              | ANY     | Lấy thông tin đầy đủ              |
| GET    | `/assignments/course/{courseId}` | ANY     | Lấy tất cả assignments của course |
| DELETE | `/assignments/{id}`              | TEACHER | Xóa assignment                    |

### Lecture APIs

| Method | Endpoint                      | Role    | Description                    |
| ------ | ----------------------------- | ------- | ------------------------------ |
| POST   | `/lectures/upload`            | TEACHER | Upload lecture file            |
| GET    | `/lectures/{id}`              | ANY     | Lấy thông tin lecture          |
| GET    | `/lectures/course/{courseId}` | ANY     | Lấy tất cả lectures của course |
| GET    | `/lectures/{id}/download`     | ANY     | Download lecture file          |
| PATCH  | `/lectures/{id}`              | TEACHER | Cập nhật lecture               |
| DELETE | `/lectures/{id}`              | TEACHER | Xóa lecture                    |

### Submission APIs

| Method | Endpoint                                 | Role    | Description              |
| ------ | ---------------------------------------- | ------- | ------------------------ |
| POST   | `/submissions/{assignmentId}/submit`     | STUDENT | Submit assignment        |
| GET    | `/submissions/{id}`                      | ANY     | Lấy thông tin submission |
| GET    | `/submissions/assignment/{assignmentId}` | TEACHER | Xem tất cả submissions   |
| GET    | `/submissions/my-submissions`            | STUDENT | Xem submissions của mình |
| GET    | `/submissions/{id}/download`             | ANY     | Download submission file |
| DELETE | `/submissions/{id}`                      | TEACHER | Xóa submission           |

## 📁 Cấu Trúc Project

```
grading-service/
├── src/
│   ├── main/
│   │   ├── java/com/devteam/gradingservice/
│   │   │   ├── config/
│   │   │   │   ├── JpaConfiguration.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AssignmentController.java
│   │   │   │   ├── LectureController.java
│   │   │   │   └── SubmissionController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateAssignmentDTO.java
│   │   │   │   │   ├── UpdateAssignmentRequestDTO.java
│   │   │   │   │   ├── CreateLectureDTO.java
│   │   │   │   │   └── UpdateLectureDTO.java
│   │   │   │   └── response/
│   │   │   │       ├── AssignmentResponse.java
│   │   │   │       ├── GetAssignmentDTO.java
│   │   │   │       ├── LectureResponse.java
│   │   │   │       └── SubmissionResponse.java
│   │   │   ├── exception/
│   │   │   │   ├── ApplicationExceptionHandler.java
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── ErrorCode.java
│   │   │   │   ├── FileStorageException.java
│   │   │   │   └── FileStorageErrorCode.java
│   │   │   ├── model/
│   │   │   │   ├── Assignment.java
│   │   │   │   ├── Lecture.java
│   │   │   │   └── Submission.java
│   │   │   ├── repository/
│   │   │   │   ├── AssignmentRepository.java
│   │   │   │   ├── LectureRepository.java
│   │   │   │   └── SubmissionRepository.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── SecurityUtils.java
│   │   │   │   ├── UserHeaderFilter.java
│   │   │   │   └── UserPrincipal.java
│   │   │   └── service/
│   │   │       ├── AssignmentService.java
│   │   │       ├── AssignmentMapper.java
│   │   │       ├── LectureService.java
│   │   │       ├── SubmissionService.java
│   │   │       └── FileStorageService.java
│   │   └── resources/
│   │       └── application.properties
├── uploads/                    # Auto-created
│   ├── lectures/              # Lecture files
│   └── submissions/           # Submission files
├── GRADING_SERVICE_API.md     # Comprehensive API documentation
├── test-grading-service.http  # HTTP test file
└── pom.xml
```

## 🔐 Security Architecture

### Authentication Flow

```
Client Request
    ↓
API Gateway (Port 8080)
    ↓
JWT Validation (RSA Public Key)
    ↓
Extract User Info (userId, email, role, name)
    ↓
Add Headers (X-User-Id, X-User-Email, X-Role, etc.)
    ↓
Forward to Grading Service (Port 8083)
    ↓
UserHeaderFilter extracts headers
    ↓
Set SecurityContext with UserPrincipal
    ↓
@PreAuthorize checks role
    ↓
Execute Controller Method
```

### Security Features

- ✅ **No JWT validation** trong service (trusts Gateway)
- ✅ **User context** từ headers via UserHeaderFilter
- ✅ **Role-based authorization** với @PreAuthorize
- ✅ **Automatic user tracking** với JPA Auditing
- ✅ **SecurityUtils** để truy cập current user

## 🗄️ Database Schema

### Assignment Table

```sql
CREATE TABLE ASSIGNMENT (
    ID VARCHAR(255) PRIMARY KEY,
    TITLE VARCHAR(255) NOT NULL,
    DESCRIPTION TEXT NOT NULL,
    COURSE_ID VARCHAR(255) NOT NULL,
    SUBMISSION_DATE TIMESTAMP NOT NULL,
    CREATE_BY VARCHAR(255) NOT NULL,
    LAST_MODIFIED_BY VARCHAR(255),
    CREATED_DATE TIMESTAMP NOT NULL,
    LAST_MODIFIED_DATE TIMESTAMP
);
```

### Lecture Table

```sql
CREATE TABLE LECTURE (
    ID VARCHAR(255) PRIMARY KEY,
    TITLE VARCHAR(255) NOT NULL,
    DESCRIPTION TEXT,
    COURSE_ID VARCHAR(255) NOT NULL,
    FILE_NAME VARCHAR(255) NOT NULL,
    FILE_PATH VARCHAR(500) NOT NULL,
    FILE_SIZE BIGINT,
    CONTENT_TYPE VARCHAR(100),
    UPLOADED_BY VARCHAR(255) NOT NULL,
    LAST_MODIFIED_BY VARCHAR(255),
    CREATED_DATE TIMESTAMP NOT NULL,
    LAST_MODIFIED_DATE TIMESTAMP
);
```

### Submission Table

```sql
CREATE TABLE SUBMISSION (
    ID VARCHAR(255) PRIMARY KEY,
    ASSIGNMENT_ID VARCHAR(255) NOT NULL,
    STUDENT_ID VARCHAR(255) NOT NULL,
    FILE_NAME VARCHAR(255) NOT NULL,
    FILE_PATH VARCHAR(500) NOT NULL,
    FILE_SIZE BIGINT,
    CONTENT_TYPE VARCHAR(100),
    SUBMITTED_DATE TIMESTAMP NOT NULL,
    STATUS VARCHAR(50),
    GRADE DOUBLE PRECISION,
    FEEDBACK TEXT,
    CREATED_DATE TIMESTAMP NOT NULL
);
```

## ⚙️ Configuration

### application.properties

```properties
# Server Configuration
server.port=8083
spring.application.name=grading-service

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/school_grading_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# File Storage Configuration
file.upload-dir=uploads
file.lecture-dir=lectures
file.submission-dir=submissions
file.max-size=10485760  # 10MB
```

## 📝 Usage Examples

### 1. Teacher tạo Assignment

```bash
POST http://localhost:8080/api/v1/grading/assignments/create
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

### 2. Teacher upload Lecture

```bash
POST http://localhost:8080/api/v1/grading/lectures/upload
Authorization: Bearer {teacher-jwt}
Content-Type: multipart/form-data

title=Introduction to Java
courseId=course-123
file=@lecture1.pdf
```

### 3. Student submit Assignment

```bash
POST http://localhost:8080/api/v1/grading/submissions/assignment-123/submit
Authorization: Bearer {student-jwt}
Content-Type: multipart/form-data

file=@my-assignment.pdf
```

**Kết quả:**

- Nếu submit trước deadline: `status = "SUBMITTED"`
- Nếu submit sau deadline: `status = "LATE"`

### 4. Student download Lecture

```bash
GET http://localhost:8080/api/v1/grading/lectures/lecture-123/download
Authorization: Bearer {student-jwt}
```

## 🧪 Testing

### Run Tests

```bash
# Sử dụng REST Client extension trong VS Code
# Mở file: test-grading-service.http
# Click "Send Request" cho từng test
```

### Test Flow

1. ✅ Teacher tạo assignment
2. ✅ Teacher upload lecture
3. ✅ Student view lectures
4. ✅ Student submit assignment
5. ✅ Teacher view all submissions
6. ✅ Teacher download submissions

## 🚨 Error Handling

### Common Errors

| Error Code                   | Description              | Solution             |
| ---------------------------- | ------------------------ | -------------------- |
| `ASSIGNMENT_NOT_FOUND`       | Assignment không tồn tại | Kiểm tra ID          |
| `LECTURE_NOT_FOUND`          | Lecture không tồn tại    | Kiểm tra ID          |
| `FILE_TOO_LARGE`             | File quá lớn (>10MB)     | Giảm kích thước file |
| `FILE_STORAGE_EXCEPTION`     | Lỗi lưu file             | Kiểm tra permissions |
| `SUBMISSION_DEADLINE_PASSED` | Quá deadline             | Submit sớm hơn       |

## 📊 Features Overview

### ✅ Đã Implement

1. **Assignment Management**

   - ✅ Create assignment với validation
   - ✅ Update assignment
   - ✅ Get basic info (id, title, courseId, createBy)
   - ✅ Get full details
   - ✅ Delete assignment
   - ✅ List by course

2. **Lecture Management**

   - ✅ Upload lecture files
   - ✅ Download lecture files
   - ✅ Update lecture metadata
   - ✅ Delete lecture (file + database)
   - ✅ List by course
   - ✅ File size limit (10MB)

3. **Submission Management**

   - ✅ Student submit assignment
   - ✅ Automatic deadline checking
   - ✅ Status tracking (SUBMITTED/LATE)
   - ✅ Resubmission support
   - ✅ Download submissions
   - ✅ List student's submissions
   - ✅ List all submissions for assignment (teacher)

4. **File Storage**

   - ✅ Organized directory structure
   - ✅ Unique file names (UUID)
   - ✅ File validation
   - ✅ Automatic cleanup on delete
   - ✅ Support multiple file types

5. **Security**
   - ✅ JWT validation via Gateway
   - ✅ Role-based authorization
   - ✅ User context extraction
   - ✅ JPA Auditing

### 🔜 Future Enhancements

- ⏳ Grade submissions
- ⏳ Feedback system
- ⏳ Bulk download
- ⏳ File type restrictions
- ⏳ Plagiarism detection
- ⏳ Assignment templates
- ⏳ Email notifications

## 🛠️ Dependencies

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

## 📞 Support

Xem thêm documentation:

- [GRADING_SERVICE_API.md](./GRADING_SERVICE_API.md) - Chi tiết API documentation
- [test-grading-service.http](./test-grading-service.http) - Test cases

## 🎯 Status

**✅ Ready for Production**

- All APIs implemented and tested
- Security properly configured
- File storage working
- Error handling complete
- Documentation complete

**Last Updated:** January 14, 2026
