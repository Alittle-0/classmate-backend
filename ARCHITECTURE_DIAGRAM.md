# 🏗️ Microservice Security Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND                                   │
│                         (React, Angular, etc.)                          │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
                                 │ HTTP Request with JWT
                                 │ Authorization: Bearer <token>
                                 ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          API GATEWAY (Port 8080)                        │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │              JwtAuthenticationFilter                              │  │
│  │  • Validate JWT token signature                                  │  │
│  │  • Extract claims (userId, email, roles, etc.)                   │  │
│  │  • Add HTTP headers:                                             │  │
│  │    - X-User-Id: 123                                              │  │
│  │    - X-User-Email: user@example.com                              │  │
│  │    - X-Roles: TEACHER,ADMIN                                      │  │
│  │    - X-Firstname: John                                           │  │
│  │    - X-Lastname: Doe                                             │  │
│  │  • Forward request to downstream service                         │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                           │
│  Routes:                                                                  │
│  • /api/v1/identity/** → Identity Service (no JWT filter)                │
│  • /api/v1/academic/** → Academic Service (with JWT filter)              │
│  • /api/v1/grading/**  → Grading Service (with JWT filter)               │
└────────────────────────────────┬─────────────────────────────────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
┌────────────────────────────┐  ┌────────────────────────────┐
│  IDENTITY SERVICE          │  │  ACADEMIC SERVICE          │
│  (Port 8081)               │  │  (Port 8082)               │
│                            │  │                            │
│  ┌──────────────────────┐  │  │  ┌──────────────────────┐  │
│  │ Spring Security      │  │  │  │ UserHeaderFilter     │  │
│  │ + JWT Filter         │  │  │  │ • Read X-* headers   │  │
│  │                      │  │  │  │ • Create UserPrinc.  │  │
│  │ Public endpoints:    │  │  │  │ • Set SecurityCtx    │  │
│  │ • /auth/login        │  │  │  └──────────────────────┘  │
│  │ • /auth/register     │  │  │                            │
│  │                      │  │  │  ┌──────────────────────┐  │
│  │ Protected:           │  │  │  │ Spring Security      │  │
│  │ • /users/**          │  │  │  │ + Method Security    │  │
│  │ • /auth/refresh      │  │  │  │                      │  │
│  └──────────────────────┘  │  │  │ @PreAuthorize        │  │
│                            │  │  │ • hasRole('TEACHER') │  │
│  ┌──────────────────────┐  │  │  │ • hasRole('ADMIN')   │  │
│  │ JWT Token Generation │  │  │  └──────────────────────┘  │
│  │ • Sign with RSA      │  │  │                            │
│  │   private key        │  │  │  Controllers:              │
│  │ • Add claims:        │  │  │  • CourseController        │
│  │   - userId           │  │  │  • StudentController       │
│  │   - email            │  │  │                            │
│  │   - roles            │  │  │  ┌──────────────────────┐  │
│  │   - firstname        │  │  │  │ SecurityUtils        │  │
│  │   - lastname         │  │  │  │ • getCurrentUser()   │  │
│  │ • Set expiration     │  │  │  │ • getCurrentUserId() │  │
│  └──────────────────────┘  │  │  │ • hasRole(role)      │  │
│                            │  │  └──────────────────────┘  │
│  ┌──────────────────────┐  │  │                            │
│  │ User Database        │  │  │  ┌──────────────────────┐  │
│  │ • Users              │  │  │  │ Academic Database    │  │
│  │ • Roles              │  │  │  │ • Courses            │  │
│  │ • Permissions        │  │  │  │ • Students           │  │
│  └──────────────────────┘  │  │  │ • Enrollments        │  │
└────────────────────────────┘  │  └──────────────────────┘  │
                                └────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                      DISCOVERY SERVER (Eureka)                          │
│                            Port 8761                                    │
│  • Service Registry                                                     │
│  • Load Balancing (lb://)                                               │
└─────────────────────────────────────────────────────────────────────────┘
```

## 📋 Request Flow Diagram

### Scenario: Teacher creates a new course

```
1. Frontend Login
   └─> POST /api/v1/identity/auth/login
       Body: { email, password }

2. Identity Service validates & returns JWT
   └─> Response: { accessToken: "eyJ...", refreshToken: "...", expiresIn: 900 }

3. Frontend stores JWT and makes request
   └─> POST /api/v1/academic/courses/create
       Header: Authorization: Bearer eyJ...
       Body: { courseName, courseCode, ... }

4. API Gateway receives request
   └─> JwtAuthenticationFilter validates token
       • Verify signature with public key
       • Check expiration
       • Extract claims

5. Gateway forwards with headers
   └─> POST http://academic-service:8082/api/v1/academic/courses/create
       Headers:
         X-User-Id: 123
         X-User-Email: teacher@school.com
         X-Roles: TEACHER
         X-Firstname: John
         X-Lastname: Doe
       Body: { courseName, courseCode, ... }

6. Academic Service processes
   └─> UserHeaderFilter extracts headers
       └─> Create UserPrincipal(userId, email, roles, ...)
           └─> Set SecurityContext with authorities [ROLE_TEACHER]

7. CourseController validates authorization
   └─> @PreAuthorize("hasRole('TEACHER')") ✓
       └─> SecurityUtils.getCurrentUser() → UserPrincipal
           └─> courseService.createCourse(dto)
               └─> Save course with creator info

8. Response flows back
   └─> Academic Service → Gateway → Frontend
       Status: 201 Created
       Body: "Course created successfully by John Doe"
```

## 🔐 Security Layers

```
Layer 1: Network Security
├─ Only API Gateway exposed to internet
├─ Internal services in private network
└─ No direct access to services

Layer 2: API Gateway Authentication
├─ JWT token validation
├─ RSA signature verification
├─ Token expiration check
└─ Extract & forward user context

Layer 3: Service Authorization
├─ Method-level security (@PreAuthorize)
├─ Role-based access control
├─ Business logic validation
└─ Resource ownership checks

Layer 4: Data Access Security
├─ JPA/Hibernate filtering
├─ Row-level security
└─ Audit logging
```

## 🎯 Key Principles

1. **Single Point of Authentication**

   - API Gateway validates JWT
   - Services trust Gateway headers
   - No duplicate token validation

2. **Zero Trust Between Services**

   - Each service validates authorization
   - Check user roles and permissions
   - Implement business-level security

3. **Stateless Architecture**

   - No server-side sessions
   - JWT contains all user context
   - Horizontally scalable

4. **Defense in Depth**
   - Network isolation
   - Gateway authentication
   - Service authorization
   - Data-level security

## 🔄 Token Lifecycle

```
┌──────────────┐
│ User Login   │
└──────┬───────┘
       │
       ▼
┌──────────────────────┐
│ Generate JWT         │
│ • Expiry: 15 minutes │
│ • Refresh: 7 days    │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Store in Frontend    │
│ • LocalStorage       │
│ • SessionStorage     │
│ • Memory             │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Include in Requests  │
│ Authorization: Bearer│
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Token Expires?       │
├─ Yes → Refresh Token │
└─ No  → Continue      │
```

## 📊 Service Responsibilities Matrix

| Responsibility            | API Gateway | Identity Service       | Academic Service  |
| ------------------------- | ----------- | ---------------------- | ----------------- |
| **JWT Creation**          | ❌          | ✅                     | ❌                |
| **JWT Validation**        | ✅          | ✅ (for own endpoints) | ❌                |
| **User Management**       | ❌          | ✅                     | ❌                |
| **Extract User Context**  | ✅          | ❌                     | ✅ (from headers) |
| **Authorization (Roles)** | ❌          | ✅                     | ✅                |
| **Business Logic**        | ❌          | ✅                     | ✅                |
| **Database Access**       | ❌          | ✅                     | ✅                |

## 🚀 Benefits of This Architecture

1. **Performance**: JWT validated once at gateway
2. **Scalability**: Stateless, easy to scale horizontally
3. **Security**: Defense in depth, multiple layers
4. **Maintainability**: Clear separation of concerns
5. **Flexibility**: Easy to add new services
6. **Monitoring**: Centralized logging at gateway
