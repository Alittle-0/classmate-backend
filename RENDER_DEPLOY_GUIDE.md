# 🚀 Hướng Dẫn Deploy Microservice Project Lên Render

## 📋 Tổng Quan Dự Án

Dự án bao gồm các service sau:
| Service | Port Local | Mô tả |
|---------|-----------|-------|
| discovery-server | 8761 | Eureka Service Registry |
| api-gateway | 8080 | API Gateway với JWT validation |
| identity-service | 8081 | Authentication & Authorization |
| academic-service | 8082 | Academic management |
| grading-service | 8083 | Grading & file upload |

**Dependencies:**

- PostgreSQL (3 databases)

---

## 📁 Cấu Trúc Thư Mục Cần Chuẩn Bị

```
backend/                    # Root folder (.git nằm ở đây)
├── discovery-server/
├── api-gateway/
├── identity-service/
├── academic-service/
├── grading-service/
├── docker-compose.yml
└── RENDER_DEPLOY_GUIDE.md
```

---

## 🗄️ BƯỚC 1: Tạo PostgreSQL Database Trên Render

### 1.1. Tạo PostgreSQL Instance

1. Đăng nhập [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"PostgreSQL"**
3. Điền thông tin:
   - **Name**: `microservice-postgres` (hoặc tên bạn muốn)
   - **Database**: `school_identity_db` (database đầu tiên)
   - **User**: để mặc định
   - **Region**: Singapore hoặc gần bạn nhất
   - **Plan**: Free (hoặc plan phù hợp)
4. Click **"Create Database"**

### 1.2. Lấy Thông Tin Kết Nối

Sau khi tạo xong, vào tab **"Info"** để lấy:

- **Internal Database URL**: Dùng cho các service trên Render
- **External Database URL**: Dùng để connect từ máy local
- **PSQL Command**: Dùng để tạo thêm database

**Ví dụ Internal URL:**

```
postgres://user:password@dpg-xxxxx-a.singapore-postgres.render.com/school_identity_db
```

### 1.3. Tạo Thêm 2 Database Còn Lại

Sử dụng **PSQL Command** từ Render hoặc dùng tool như DBeaver/pgAdmin:

```sql
-- Kết nối vào PostgreSQL instance trước
-- Sau đó chạy:
CREATE DATABASE school_academic_db;
CREATE DATABASE school_grading_db;
```

**Hoặc dùng terminal:**

```bash
# Copy PSQL Command từ Render Dashboard rồi chạy
psql "postgres://user:password@host/school_identity_db"

# Sau đó trong psql shell:
CREATE DATABASE school_academic_db;
CREATE DATABASE school_grading_db;
\q
```

---

## � BƯỚC 2: Chuẩn Bị Cấu Hình Cho Mỗi Service

### 2.1. Tạo File `application-prod.properties` Cho Mỗi Service

#### 🔹 discovery-server/src/main/resources/application-prod.properties

```properties
server.port=${PORT:8761}
spring.application.name=discovery-server

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Cho phép các service khác tìm thấy Eureka
eureka.server.enable-self-preservation=false
eureka.instance.hostname=${RENDER_EXTERNAL_HOSTNAME:localhost}
eureka.instance.prefer-ip-address=false
```

#### 🔹 identity-service/src/main/resources/application-prod.properties

```properties
server.port=${PORT:8081}
spring.application.name=identity-service

# Database - Sử dụng Internal URL từ Render
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
eureka.instance.prefer-ip-address=true

# JWT
app.security.jwt.access-token-expiration=${JWT_ACCESS_EXPIRATION:86400000}
app.security.jwt.refresh-token-expiration=${JWT_REFRESH_EXPIRATION:86400000}

# Swagger - Disable in production (optional)
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

#### 🔹 academic-service/src/main/resources/application-prod.properties

```properties
server.port=${PORT:8082}
spring.application.name=academic-service

# Database - Sử dụng database riêng
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
eureka.instance.prefer-ip-address=true
```

#### 🔹 grading-service/src/main/resources/application-prod.properties

```properties
server.port=${PORT:8083}
spring.application.name=grading-service

# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
eureka.instance.prefer-ip-address=true

# File Upload - Sử dụng /tmp hoặc cloud storage
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
file.upload-dir=/tmp/uploads
file.lecture-dir=lectures
file.submission-dir=submissions
file.max-size=10485760
```

#### 🔹 api-gateway/src/main/resources/application-prod.properties

```properties
server.port=${PORT:8080}
spring.application.name=api-gateway

# Eureka
eureka.client.service-url.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
eureka.instance.prefer-ip-address=true

# Gateway Routes - Sử dụng service name thay vì lb://
spring.cloud.gateway.routes[0].id=identity-service
spring.cloud.gateway.routes[0].uri=${IDENTITY_SERVICE_URL:lb://identity-service}
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/v1/identity/**

spring.cloud.gateway.routes[1].id=academic-service
spring.cloud.gateway.routes[1].uri=${ACADEMIC_SERVICE_URL:lb://academic-service}
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/v1/academic/**
spring.cloud.gateway.routes[1].filters[0]=JwtAuthenticationFilter

spring.cloud.gateway.routes[2].id=grading-service
spring.cloud.gateway.routes[2].uri=${GRADING_SERVICE_URL:lb://grading-service}
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/v1/grading/**
spring.cloud.gateway.routes[2].filters[0]=JwtAuthenticationFilter

# CORS - Cập nhật domain thực tế
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=${CORS_ORIGINS:*}
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
```

---

## 🐳 BƯỚC 3: Tạo Dockerfile Cho Mỗi Service

### 3.1. Dockerfile Chung (áp dụng cho tất cả service)

Tạo file `Dockerfile` trong mỗi thư mục service:

#### discovery-server/Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### identity-service/Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### academic-service/Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### grading-service/Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Tạo thư mục uploads
RUN mkdir -p /tmp/uploads/lectures /tmp/uploads/submissions

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### api-gateway/Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

---

## 🌐 BƯỚC 4: Deploy Lên Render

### Thứ Tự Deploy (QUAN TRỌNG!)

1. **Discovery Server** (trước tiên)
2. **Identity Service**
3. **Academic Service**
4. **Grading Service**
5. **API Gateway** (cuối cùng)

### 4.1. Deploy Discovery Server

1. Vào Render Dashboard → **"New +"** → **"Web Service"**
2. Connect GitHub repo của bạn
3. Cấu hình:
   - **Name**: `discovery-server`
   - **Root Directory**: `discovery-server`
   - **Environment**: Docker
   - **Plan**: Free hoặc phù hợp
4. **Environment Variables**:
   ```
   PORT=8761
   ```
5. Click **"Create Web Service"**
6. **⚠️ Đợi deploy xong và copy URL** (ví dụ: `https://classmate-discovery-server.onrender.com`)

### 4.2. Deploy Identity Service

1. **"New +"** → **"Web Service"**
2. Cấu hình:
   - **Name**: `identity-service`
   - **Root Directory**: `identity-service`
   - **Environment**: Docker
3. **Environment Variables**:

   ```
   PORT=8081
   DATABASE_URL=jdbc:postgresql://@dpg-d5lv02cmrvns73epukqg-a/school_identity_db
   DATABASE_USER=[your_db_user]
   DATABASE_PASSWORD=[your_db_password]
   EUREKA_URL=https://discovery-server-xxxx.onrender.com/eureka/
   JWT_ACCESS_EXPIRATION=86400000
   JWT_REFRESH_EXPIRATION=86400000
   ```

   **⚠️ Lưu ý về DATABASE_URL:**
   - Lấy từ Render PostgreSQL → Internal Database URL
   - Chuyển từ format `postgres://user:pass@host/db` sang `jdbc:postgresql://host/db`
   - Tách riêng user và password

### 4.3. Deploy Academic Service

**Environment Variables**:

```
PORT=8082
DATABASE_URL=jdbc:postgresql://[INTERNAL_HOST]/school_academic_db
DATABASE_USER=[your_db_user]
DATABASE_PASSWORD=[your_db_password]
EUREKA_URL=https://discovery-server-xxxx.onrender.com/eureka/
```

### 4.4. Deploy Grading Service

**Environment Variables**:

```
PORT=8083
DATABASE_URL=jdbc:postgresql://[INTERNAL_HOST]/school_grading_db
DATABASE_USER=[your_db_user]
DATABASE_PASSWORD=[your_db_password]
EUREKA_URL=https://discovery-server-xxxx.onrender.com/eureka/
```

### 4.5. Deploy API Gateway

**Environment Variables**:

```
PORT=8080
EUREKA_URL=https://discovery-server-xxxx.onrender.com/eureka/
CORS_ORIGINS=https://your-frontend-domain.com
```

**⚠️ Tùy chọn: Nếu Eureka không hoạt động tốt trên Render**

Thay vì dùng Eureka, có thể direct URL:

```
IDENTITY_SERVICE_URL=https://identity-service-xxxx.onrender.com
ACADEMIC_SERVICE_URL=https://academic-service-xxxx.onrender.com
GRADING_SERVICE_URL=https://grading-service-xxxx.onrender.com
```

---

## ⚠️ BƯỚC 5: Xử Lý Các Vấn Đề Thường Gặp

### 5.1. RSA Keys Cho JWT

Keys hiện tại nằm trong `keys/local-only/`. Có 2 cách xử lý:

**Cách 1: Commit keys vào repo (KHÔNG KHUYẾN KHÍCH cho production)**

- Đổi tên folder `local-only` → `prod`
- Cập nhật path trong code

**Cách 2: Sử dụng Environment Variable (KHUYẾN KHÍCH)**

Sửa `KeyUtils.java` để đọc key từ environment variable:

```java
// Thay đổi trong identity-service và api-gateway

public static PrivateKey loadPrivateKey() throws Exception {
    String keyContent = System.getenv("JWT_PRIVATE_KEY");
    if (keyContent == null) {
        // Fallback to file for local development
        return loadPrivateKeyFromFile("keys/local-only/private-secret.pem");
    }
    // Parse key from environment variable
    keyContent = keyContent
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
    byte[] decoded = Base64.getDecoder().decode(keyContent);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    return KeyFactory.getInstance("RSA").generatePrivate(spec);
}
```

Sau đó thêm Environment Variable trên Render:

```
JWT_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBg...[nội dung key]...\n-----END PRIVATE KEY-----
JWT_PUBLIC_KEY=-----BEGIN PUBLIC KEY-----\nMIIBIjANBg...[nội dung key]...\n-----END PUBLIC KEY-----
```

### 5.2. File Upload (Grading Service)

Render sử dụng ephemeral filesystem - files sẽ bị xóa khi redeploy!

**Giải pháp:**

- Sử dụng cloud storage (AWS S3, Cloudinary, etc.)
- Hoặc Render Disk (có phí)

### 5.3. Cold Start

Free tier Render sẽ sleep sau 15 phút không có traffic. Request đầu tiên sẽ mất ~30s.

**Giải pháp:**

- Upgrade lên paid plan
- Hoặc dùng service như UptimeRobot để ping định kỳ

### 5.4. Eureka Trên Cloud

Eureka có thể gặp vấn đề với dynamic IP trên cloud. Nếu gặp lỗi:

**Giải pháp 1**: Dùng Direct URL thay vì service discovery (xem 4.5)

**Giải pháp 2**: Cấu hình thêm cho Eureka client:

```properties
eureka.instance.hostname=${RENDER_EXTERNAL_HOSTNAME}
eureka.instance.non-secure-port=443
eureka.instance.secure-port-enabled=true
eureka.instance.non-secure-port-enabled=false
```

---

## ✅ BƯỚC 6: Kiểm Tra Sau Deploy

### 6.1. Kiểm tra Discovery Server

```
https://discovery-server-xxxx.onrender.com/
```

Phải thấy Eureka Dashboard với các service đã đăng ký.

### 6.2. Kiểm tra API Gateway

```bash
# Health check (nếu có)
curl https://api-gateway-xxxx.onrender.com/actuator/health

# Test identity service qua gateway
curl https://api-gateway-xxxx.onrender.com/api/v1/identity/health
```

### 6.3. Test Full Flow

```bash
# Register
curl -X POST https://api-gateway-xxxx.onrender.com/api/v1/identity/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test", "password":"test123", "email":"test@test.com"}'

# Login
curl -X POST https://api-gateway-xxxx.onrender.com/api/v1/identity/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test", "password":"test123"}'
```

---

## 📊 Tổng Kết Chi Phí Render (Free Tier)

| Resource     | Free Tier Limit                  |
| ------------ | -------------------------------- |
| Web Services | 750 hours/month (shared)         |
| PostgreSQL   | 1 database, 1GB storage, 90 days |
| Static Sites | 100GB bandwidth                  |

**Lưu ý**: Free tier có giới hạn, phù hợp cho development/testing.

---

## 🔗 Tài Liệu Tham Khảo

- [Render Docs - Web Services](https://render.com/docs/web-services)
- [Render Docs - PostgreSQL](https://render.com/docs/databases)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)

---

## 📌 Checklist Deploy

- [ ] Tạo PostgreSQL trên Render
- [ ] Tạo 3 databases (identity, academic, grading)
- [ ] Tạo file `application-prod.properties` cho mỗi service
- [ ] Tạo `Dockerfile` cho mỗi service
- [ ] Commit và push lên GitHub
- [ ] Deploy Discovery Server
- [ ] Deploy Identity Service
- [ ] Deploy Academic Service
- [ ] Deploy Grading Service
- [ ] Deploy API Gateway
- [ ] Test các endpoint
- [ ] Cấu hình CORS cho frontend

---

**Chúc bạn deploy thành công! 🎉**
