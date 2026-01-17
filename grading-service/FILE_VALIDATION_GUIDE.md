# 📎 File Upload Validation Guide

## 🎯 Allowed File Types

Grading Service chỉ cho phép upload các loại file sau:

### ✅ **Accepted File Types**

| Category               | Extensions      | MIME Types                                                                                                     | Use Case                          |
| ---------------------- | --------------- | -------------------------------------------------------------------------------------------------------------- | --------------------------------- |
| **PDF Documents**      | `.pdf`          | `application/pdf`                                                                                              | Slides, documents, assignments    |
| **PowerPoint**         | `.ppt`, `.pptx` | `application/vnd.ms-powerpoint`<br>`application/vnd.openxmlformats-officedocument.presentationml.presentation` | Lecture presentations             |
| **Word Documents**     | `.doc`, `.docx` | `application/msword`<br>`application/vnd.openxmlformats-officedocument.wordprocessingml.document`              | Text documents, reports           |
| **Excel Spreadsheets** | `.xls`, `.xlsx` | `application/vnd.ms-excel`<br>`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`              | Data, calculations                |
| **ZIP Archives**       | `.zip`          | `application/zip`<br>`application/x-zip-compressed`                                                            | Project folders, multiple files   |
| **RAR Archives**       | `.rar`          | `application/x-rar-compressed`<br>`application/vnd.rar`<br>`application/octet-stream`                          | Project folders, compressed files |

---

## 🚫 **Rejected File Types**

Các loại file sau sẽ bị **TỪ CHỐI**:

- ❌ Text files (`.txt`)
- ❌ Images (`.jpg`, `.png`, `.gif`, `.bmp`)
- ❌ Videos (`.mp4`, `.avi`, `.mov`)
- ❌ Audio (`.mp3`, `.wav`)
- ❌ Executables (`.exe`, `.bat`, `.sh`)
- ❌ Code archives (`.tar`, `.gz`, `.7z`) - _chỉ `.zip` và `.rar` được phép_
- ❌ Any other extensions not listed above

---

## 📏 File Size Limits

- **Maximum file size:** 10 MB (10,485,760 bytes)
- Files lớn hơn sẽ bị reject với error: `FILE_TOO_LARGE`

---

## 🔍 Validation Logic

### **Kiểm Tra 2 Lớp:**

#### 1. **Extension Validation** (Bắt buộc)

```java
Allowed extensions:
- .pdf
- .ppt, .pptx
- .doc, .docx
- .xls, .xlsx
- .zip
- .rar
```

**Logic:**

- Lấy extension từ filename
- Check có trong danh sách cho phép không
- Case-insensitive: `.PDF` = `.pdf`

#### 2. **Content Type Validation** (Warning only)

```java
Allowed MIME types:
- application/pdf
- application/vnd.ms-powerpoint
- application/vnd.openxmlformats-officedocument.presentationml.presentation
- application/msword
- application/vnd.openxmlformats-officedocument.wordprocessingml.document
- application/vnd.ms-excel
- application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
- application/zip
- application/x-zip-compressed
- application/x-rar-compressed
- application/vnd.rar
- application/octet-stream
```

**Note:** Content type mismatch chỉ ghi log warning, không reject file vì:

- Browsers khác nhau gửi content types khác nhau
- RAR files thường có content type `application/octet-stream`
- Extension validation đã đủ để đảm bảo file type

---

## 💡 Examples

### ✅ **Valid Uploads**

```http
# PDF Lecture
POST /api/v1/grading/lectures/upload
file: lecture-notes.pdf
→ ✅ ACCEPTED

# PowerPoint Presentation
POST /api/v1/grading/lectures/upload
file: slides.pptx
→ ✅ ACCEPTED

# Word Document
POST /api/v1/grading/lectures/upload
file: syllabus.docx
→ ✅ ACCEPTED

# Excel Spreadsheet
POST /api/v1/grading/lectures/upload
file: grades.xlsx
→ ✅ ACCEPTED

# ZIP Archive (project code)
POST /api/v1/grading/submissions/{id}/submit
file: my-project.zip
→ ✅ ACCEPTED (can contain folders and multiple files)

# RAR Archive
POST /api/v1/grading/submissions/{id}/submit
file: assignment.rar
→ ✅ ACCEPTED (can contain folders and multiple files)
```

### ❌ **Invalid Uploads**

```http
# Text file
POST /api/v1/grading/lectures/upload
file: notes.txt
→ ❌ REJECTED
Error: INVALID_FILE_TYPE: ".txt"

# Image file
POST /api/v1/grading/submissions/{id}/submit
file: screenshot.png
→ ❌ REJECTED
Error: INVALID_FILE_TYPE: ".png"

# Video file
POST /api/v1/grading/lectures/upload
file: tutorial.mp4
→ ❌ REJECTED
Error: INVALID_FILE_TYPE: ".mp4"

# File without extension
POST /api/v1/grading/lectures/upload
file: document
→ ❌ REJECTED
Error: INVALID_FILE_TYPE: "no extension"

# File too large (>10MB)
POST /api/v1/grading/submissions/{id}/submit
file: huge-project.zip (15 MB)
→ ❌ REJECTED
Error: FILE_TOO_LARGE: "File size exceeds maximum limit of 10 MB"
```

---

## 🗂️ ZIP/RAR Archive Support

### **Folders Inside Archives:**

ZIP và RAR files **có thể chứa folders** - hoàn toàn được support!

**Ví dụ cấu trúc ZIP hợp lệ:**

```
assignment-project.zip
├── src/
│   ├── main/
│   │   └── Application.java
│   └── test/
│       └── TestApp.java
├── resources/
│   └── application.properties
├── pom.xml
└── README.md
```

**Khi extract:**

- Tất cả folders và files trong ZIP/RAR được giữ nguyên structure
- Student/Teacher có thể upload entire project folder
- Không giới hạn số lượng files/folders bên trong

**Use Cases:**

- 📦 Student submit source code project (nhiều files + folders)
- 📦 Teacher upload code examples với full structure
- 📦 Upload multiple documents trong 1 archive
- 📦 Share project với dependencies

---

## 🔧 Configuration

### **application.properties**

```properties
# Maximum file size: 10MB
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# File storage configuration
file.upload-dir=uploads
file.lecture-dir=lectures
file.submission-dir=submissions
file.max-size=10485760  # 10MB in bytes
```

### **Thay đổi file size limit:**

```properties
# Tăng lên 20MB
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
file.max-size=20971520  # 20MB in bytes
```

---

## 📊 Error Codes

| Error Code               | Message                                      | HTTP Status | Meaning                   |
| ------------------------ | -------------------------------------------- | ----------- | ------------------------- |
| `INVALID_FILE_TYPE`      | File type not allowed: {extension}           | 400         | Extension không được phép |
| `FILE_TOO_LARGE`         | File size exceeds maximum limit of {size} MB | 400         | File quá lớn              |
| `FILE_STORAGE_EXCEPTION` | Could not store file. Please try again!      | 500         | Lỗi lưu file              |
| `FILE_NOT_FOUND`         | File not found: {fileName}                   | 404         | File không tồn tại        |

---

## 🎯 Best Practices

### **Cho Teachers:**

1. ✅ Upload PDF cho lecture slides (dễ view trên mọi device)
2. ✅ Upload PPTX nếu muốn students edit
3. ✅ Upload DOCX cho tài liệu text
4. ✅ Upload XLSX cho bảng điểm, data
5. ✅ Compress multiple files vào ZIP trước khi upload
6. ✅ Check file size < 10MB trước upload

### **Cho Students:**

1. ✅ Submit PDF cho văn bản (không thay đổi format)
2. ✅ Submit ZIP/RAR cho source code projects
3. ✅ Đặt tên file rõ ràng: `StudentName_Assignment1.pdf`
4. ✅ Compress code projects để giảm size
5. ✅ Test ZIP/RAR extract được không trước khi submit
6. ⚠️ Không rename extension để bypass validation (sẽ không work)

### **Cho Developers:**

1. ✅ Validation chạy trước khi lưu file (fail fast)
2. ✅ Log warning cho content type mismatch
3. ✅ Extension check case-insensitive
4. ✅ Clear error messages cho users
5. ✅ UUID filenames tránh conflicts

---

## 🧪 Testing

### **Test Cases:**

```bash
# Test valid file types
1. Upload PDF → Should pass
2. Upload PPTX → Should pass
3. Upload DOCX → Should pass
4. Upload XLSX → Should pass
5. Upload ZIP → Should pass
6. Upload RAR → Should pass

# Test invalid file types
7. Upload TXT → Should fail (INVALID_FILE_TYPE)
8. Upload JPG → Should fail (INVALID_FILE_TYPE)
9. Upload MP4 → Should fail (INVALID_FILE_TYPE)
10. Upload no extension → Should fail (INVALID_FILE_TYPE)

# Test file size
11. Upload 5MB file → Should pass
12. Upload 15MB file → Should fail (FILE_TOO_LARGE)

# Test archives with folders
13. Upload ZIP with folders → Should pass (folders preserved)
14. Upload RAR with folders → Should pass (folders preserved)
```

---

## 📝 Implementation Details

### **Code Location:**

```
FileStorageService.java
└── validateFile() method
    ├── Check empty file
    ├── Check file size
    ├── Validate extension
    └── Validate content type (warning only)
```

### **Constants:**

```java
ALLOWED_EXTENSIONS = [
    ".pdf",
    ".ppt", ".pptx",
    ".doc", ".docx",
    ".xls", ".xlsx",
    ".zip", ".rar"
]

ALLOWED_CONTENT_TYPES = [
    "application/pdf",
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/zip",
    "application/x-zip-compressed",
    "application/x-rar-compressed",
    "application/vnd.rar",
    "application/octet-stream"
]
```

---

## 🔒 Security Considerations

1. **Path Traversal Prevention:**

   - Filename với `..` bị reject
   - `.normalize()` trên paths

2. **Extension Validation:**

   - Không tin tưởng browser content type
   - Check extension trước

3. **File Size Limits:**

   - Prevent DoS attacks
   - Server không bị quá tải

4. **UUID Filenames:**
   - Tránh name collisions
   - Không expose original filenames trong URLs

---

## 📞 Support

Nếu cần thêm file types:

1. Update `ALLOWED_EXTENSIONS` list
2. Update `ALLOWED_CONTENT_TYPES` list
3. Update documentation
4. Add test cases

**Current Status:** ✅ Fully Implemented and Tested

**Last Updated:** January 14, 2026
