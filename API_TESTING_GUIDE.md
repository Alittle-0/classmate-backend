# 🧪 API Testing Guide

## 📋 Overview

File `test-all-apis.http` chứa **đầy đủ tất cả test cases** cho Student Management Microservices, bao gồm:

- ✅ 15 sections với 80+ test cases
- ✅ Tất cả endpoints của Identity Service
- ✅ Tất cả endpoints của Academic Service
- ✅ Security testing
- ✅ Validation testing
- ✅ Error scenarios
- ✅ Integration testing

---

## 🚀 Setup Trước Khi Test

### 1. Start Tất Cả Services

```bash
# Terminal 1 - Discovery Server
cd discovery-server
mvn spring-boot:run

# Terminal 2 - Identity Service
cd identity-service
mvn spring-boot:run

# Terminal 3 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 4 - Academic Service
cd academic-service
mvn spring-boot:run
```

### 2. Đợi Services Khởi Động

Kiểm tra:

- Discovery Server: http://localhost:8761
- Identity Service: http://localhost:8081/api/v1/identity/health
- API Gateway: http://localhost:8080
- Academic Service: http://localhost:8082

---

## 📝 Cách Sử Dụng File Test

### Trong VS Code với REST Client Extension:

1. **Mở file**: `test-all-apis.http`

2. **Click "Send Request"** ở trên mỗi request

3. **Xem response** ở panel bên phải

### Hoặc dùng cURL:

Copy các request và convert sang cURL format.

---

## 🎯 Test Flow Được Khuyến Nghị

### Phase 1: Setup Data (Sections 1-3)

```
1. Health Check
   └─> GET /health

2. Register Users
   ├─> POST /auth/register (Teacher)
   ├─> POST /auth/register (Student)
   └─> POST /auth/register (Admin)

3. Login to Get Tokens
   ├─> POST /auth/login (Teacher)
   ├─> POST /auth/login (Student)
   └─> POST /auth/login (Admin)

   👉 IMPORTANT: Copy tokens vào variables
```

### Phase 2: Test User Management (Sections 4, 10, 11)

```
4. User Profile
   ├─> GET /me/ (Get own profile)
   ├─> PATCH /me/ (Update profile)
   └─> POST /me/password (Change password)

10. Token Management
    ├─> POST /auth/refresh
    └─> POST /auth/logout

11. Account Management
    ├─> POST /me/deactivate
    ├─> POST /me/reactivate
    └─> DELETE /me/delete
```

### Phase 3: Test Course Management (Sections 5-9)

```
5. Create Courses (Teacher)
   └─> POST /courses/create

6. Retrieve Courses
   ├─> GET /courses/all
   ├─> GET /courses/{id}
   └─> GET /courses/courses

7. Update Courses (Teacher)
   └─> PATCH /courses/{id}

8. Course Membership
   ├─> PATCH /courses/invite/{code}
   ├─> PATCH /courses/{id}/{memberId}
   └─> PATCH /courses/{id}/leave

9. Delete Courses (Teacher)
   └─> DELETE /courses/{id}
```

### Phase 4: Security & Validation (Sections 12-13)

```
12. Security Testing
    ├─> Test invalid JWT
    ├─> Test expired JWT
    ├─> Test role violations
    └─> Test missing auth

13. Validation Testing
    ├─> Test email validation
    ├─> Test password validation
    └─> Test required fields
```

### Phase 5: Integration Testing (Section 14)

```
14. Complete Flow
    Register → Login → Create Course → Join Course
```

---

## 🔧 Update Variables

Sau khi login, **copy tokens** vào variables ở đầu file:

```http
### VARIABLES
@baseUrl = http://localhost:8080
@teacherToken = eyJhbGc... ← Paste token here
@studentToken = eyJhbGc... ← Paste token here
@courseId = COURSE-123  ← Update after creating course
```

---

## ✅ Test Checklist

### Identity Service APIs

#### Authentication

- [ ] POST `/auth/register` - Register new user
- [ ] POST `/auth/login` - Login user
- [ ] POST `/auth/refresh` - Refresh access token
- [ ] POST `/auth/logout` - Logout user

#### User Profile

- [ ] GET `/me/` - Get own profile
- [ ] GET `/me/{userId}` - Get user by ID
- [ ] PATCH `/me/` - Update profile
- [ ] POST `/me/password` - Change password
- [ ] POST `/me/deactivate` - Deactivate account
- [ ] POST `/me/reactivate` - Reactivate account
- [ ] DELETE `/me/delete` - Delete account permanently

#### Health Check

- [ ] GET `/health` - Service health status

---

### Academic Service APIs

#### Course Management

- [ ] GET `/courses/all` - Get all courses
- [ ] GET `/courses/{id}` - Get course by ID
- [ ] GET `/courses/courses` - Get my courses
- [ ] POST `/courses/create` - Create course (TEACHER)
- [ ] PATCH `/courses/{id}` - Update course (TEACHER)
- [ ] DELETE `/courses/{id}` - Delete course (TEACHER)

#### Course Membership

- [ ] PATCH `/courses/invite/{code}` - Join via invite
- [ ] PATCH `/courses/{id}/{memberId}` - Remove member (TEACHER)
- [ ] PATCH `/courses/{id}/leave` - Leave course (STUDENT)

---

## 🎯 Expected Status Codes

| Scenario                  | Status Code               | Meaning                |
| ------------------------- | ------------------------- | ---------------------- |
| Success - GET/POST        | 200 OK                    | Request successful     |
| Success - POST create     | 201 Created               | Resource created       |
| Success - PATCH/DELETE    | 204 No Content            | Action completed       |
| Client Error - Validation | 400 Bad Request           | Invalid input          |
| Client Error - Auth       | 401 Unauthorized          | Missing/invalid token  |
| Client Error - Permission | 403 Forbidden             | No permission          |
| Client Error - Not Found  | 404 Not Found             | Resource doesn't exist |
| Client Error - Conflict   | 409 Conflict              | Duplicate resource     |
| Server Error              | 500 Internal Server Error | Server problem         |

---

## 🔍 Common Issues & Solutions

### Issue 1: 401 Unauthorized

**Problem:** Token không hợp lệ hoặc expired

**Solution:**

1. Login lại để lấy token mới
2. Copy token và update variable
3. Kiểm tra format: `Authorization: Bearer <token>`

### Issue 2: 403 Forbidden

**Problem:** User không có quyền

**Solution:**

1. Kiểm tra role của user (TEACHER/STUDENT/ADMIN)
2. Đảm bảo dùng đúng token cho endpoint
3. Xem @PreAuthorize annotation trên endpoint

### Issue 3: 404 Not Found

**Problem:** Resource không tồn tại

**Solution:**

1. Kiểm tra ID có đúng không
2. Tạo resource trước khi test
3. Update variables với ID chính xác

### Issue 4: 400 Bad Request

**Problem:** Dữ liệu không hợp lệ

**Solution:**

1. Kiểm tra required fields
2. Kiểm tra format (email, password, etc.)
3. Xem validation rules trong DTO

### Issue 5: Connection Refused

**Problem:** Service chưa start

**Solution:**

1. Start tất cả services
2. Đợi services khởi động xong
3. Kiểm tra ports không bị conflict

---

## 📊 Test Coverage

### By Service

**Identity Service:**

- ✅ Authentication: 6 test cases
- ✅ User Profile: 7 test cases
- ✅ Token Management: 3 test cases
- ✅ Account Management: 3 test cases
- **Total: 19 test cases**

**Academic Service:**

- ✅ Course Management: 15 test cases
- ✅ Course Membership: 8 test cases
- **Total: 23 test cases**

**Security & Validation:**

- ✅ Security Testing: 12 test cases
- ✅ Validation Testing: 8 test cases
- ✅ Integration Testing: 5 test cases
- **Total: 25 test cases**

**Grand Total: 67+ test cases**

---

## 🎓 Testing Best Practices

### 1. Test in Order

Chạy theo thứ tự sections để có data cần thiết

### 2. Save Important Data

```
✅ Save user IDs sau khi register
✅ Save tokens sau khi login
✅ Save course IDs sau khi create
✅ Save invite codes từ database
```

### 3. Clean Up After Testing

```
- Delete test courses
- Delete test users
- Clear test data from database
```

### 4. Document Failures

```
- Note which tests failed
- Check error messages
- Review logs in services
```

### 5. Test Edge Cases

```
✅ Empty strings
✅ Very long strings
✅ Special characters
✅ Null values
✅ Invalid IDs
```

---

## 🐛 Debugging Tips

### Check Service Logs

```bash
# Identity Service logs
cd identity-service
mvn spring-boot:run

# Academic Service logs
cd academic-service
mvn spring-boot:run
```

### Check Gateway Logs

```bash
cd api-gateway
mvn spring-boot:run
```

### Verify JWT Token

1. Copy token
2. Go to https://jwt.io
3. Paste token to decode
4. Check claims: userId, email, role, exp

### Check Database

```sql
-- Check users
SELECT * FROM users;

-- Check courses
SELECT * FROM courses;

-- Check members
SELECT * FROM members;
```

---

## 📈 Performance Testing

### Load Testing

Run multiple requests concurrently:

```bash
# Use Apache Bench
ab -n 100 -c 10 -H "Authorization: Bearer <token>" \
   http://localhost:8080/api/v1/academic/courses/all

# Use wrk
wrk -t4 -c100 -d30s -H "Authorization: Bearer <token>" \
    http://localhost:8080/api/v1/academic/courses/all
```

### Response Time Expectations

- Health check: < 50ms
- Get courses: < 200ms
- Create course: < 300ms
- Login: < 500ms

---

## 🎉 Success Criteria

Test suite passes nếu:

- ✅ Tất cả health checks return 200
- ✅ Register/Login flow hoạt động
- ✅ JWT authentication và authorization hoạt động
- ✅ CRUD operations cho courses hoạt động
- ✅ Role-based access control hoạt động đúng
- ✅ Validation errors được handle đúng
- ✅ Không có 500 errors

---

## 📚 Additional Resources

- **Security Guide**: `MICROSERVICE_SECURITY_GUIDE.md`
- **Architecture**: `ARCHITECTURE_DIAGRAM.md`
- **Setup Checklist**: `SETUP_CHECKLIST.md`
- **Vietnamese Guide**: `TOM_TAT_TIENG_VIET.md`

---

## 🆘 Need Help?

### Common Commands

```bash
# Check if services are running
curl http://localhost:8761  # Discovery
curl http://localhost:8081/api/v1/identity/health  # Identity
curl http://localhost:8080  # Gateway

# View service logs
cd <service-directory>
mvn spring-boot:run

# Build and run
mvn clean install
mvn spring-boot:run

# Run tests
mvn test
```

---

**Happy Testing! 🚀**

Last Updated: December 13, 2025
