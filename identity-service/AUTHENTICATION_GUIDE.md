# Authentication API - Cookie-Based Refresh Token

## Overview
The authentication system has been updated to use HTTP-only cookies for storing refresh tokens instead of passing them in request bodies. This provides better security as the refresh token is not accessible via JavaScript.

## Changes Made

### 1. **Refresh Token API Updated**
- **Endpoint**: `POST /api/v1/identity/auth/refresh`
- **Before**: Required `refreshToken` in the request body
- **After**: Reads `refreshToken` from HTTP-only cookie automatically

#### Old Request Format (No longer used):
```json
POST /api/v1/identity/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOi..."
}
```

#### New Request Format:
```http
POST /api/v1/identity/auth/refresh
Content-Type: application/json

### No body required - cookie is sent automatically by browser
```

### 2. **Logout API Added**
- **Endpoint**: `POST /api/v1/identity/auth/logout`
- **Purpose**: Clears the refresh token cookie from the browser
- **Response**: 200 OK with empty body

## How It Works

### Login Flow:
1. User calls `POST /api/v1/identity/auth/login` with email and password
2. Backend generates both access token and refresh token
3. **Access token** is returned in the response body (store in localStorage)
4. **Refresh token** is set as an HTTP-only cookie
5. Frontend stores the access token in localStorage

### Refresh Flow:
1. When access token expires, call `POST /api/v1/identity/auth/refresh`
2. Browser automatically sends the refresh token cookie
3. Backend extracts token from cookie using `@CookieValue` annotation
4. Backend validates the refresh token and generates a new access token
5. Returns new access token in response body
6. Refresh token cookie is refreshed/renewed

### Logout Flow:
1. User calls `POST /api/v1/identity/auth/logout`
2. Backend clears the refresh token cookie (sets maxAge=0)
3. Frontend should also clear the access token from localStorage
4. User is fully logged out

## Security Benefits

1. **HTTP-Only Cookie**: JavaScript cannot access the refresh token, preventing XSS attacks
2. **SameSite Policy**: Cookie is sent with `SameSite=None` for cross-origin requests
3. **Secure Flag**: Set to `false` for development (should be `true` in production with HTTPS)
4. **Path Restriction**: Cookie is scoped to `/` path

## Frontend Integration

### Login:
```javascript
// Login request
const response = await fetch('http://localhost:8080/api/v1/identity/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password }),
  credentials: 'include' // Important: include cookies
});

const data = await response.json();
// Store access token in localStorage
localStorage.setItem('accessToken', data.accessToken);
```

### Refresh Token:
```javascript
// Refresh token request
const response = await fetch('http://localhost:8080/api/v1/identity/auth/refresh', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include' // Important: include cookies (sends refresh token automatically)
});

const data = await response.json();
// Update access token in localStorage
localStorage.setItem('accessToken', data.accessToken);
```

### Logout:
```javascript
// Logout request
await fetch('http://localhost:8080/api/v1/identity/auth/logout', {
  method: 'POST',
  credentials: 'include' // Important: include cookies
});

// Clear access token from localStorage
localStorage.removeItem('accessToken');
```

## Important Notes

1. **credentials: 'include'**: Always include this option in fetch requests to send/receive cookies
2. **CORS Configuration**: Ensure your backend CORS configuration allows credentials
3. **Production**: Set `secure(true)` for cookies when using HTTPS in production
4. **RefreshRequest DTO**: This DTO is no longer used for the refresh endpoint but can be kept for backward compatibility or removed if not needed elsewhere

## API Endpoints Summary

| Endpoint | Method | Cookie Required | Body Required | Response |
|----------|--------|----------------|---------------|----------|
| `/api/v1/identity/auth/login` | POST | No | Yes (email, password) | Access token + Sets cookie |
| `/api/v1/identity/auth/refresh` | POST | Yes (refreshToken) | No | New access token |
| `/api/v1/identity/auth/logout` | POST | No | No | 200 OK |
| `/api/v1/identity/auth/register` | POST | No | Yes (user details) | 201 Created |

