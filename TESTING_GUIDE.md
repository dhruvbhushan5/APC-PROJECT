# 🔍 Hotel Reservation System - Testing Guide

## ✅ **How to Check if Your Project is Working**

### **Method 1: Quick Visual Check (Browser)**
1. **Swagger UI** - Open in browser:
   ```
   http://localhost:8083/swagger-ui.html
   ```
   ✅ **Success**: You see the API documentation with all endpoints
   ❌ **Failure**: Page doesn't load or shows error

2. **H2 Database Console** - Open in browser:
   ```
   http://localhost:8083/h2-console
   ```
   - **JDBC URL**: `jdbc:h2:mem:hotel_user_db`
   - **User**: `sa`
   - **Password**: (leave empty)
   
   ✅ **Success**: You can connect and see database tables
   ❌ **Failure**: Connection refused or login fails

3. **Demo Page** - Open in browser:
   ```
   file:///c:/Users/VICTUS/OneDrive/Desktop/hotel-reservation/simple-demo.html
   ```
   ✅ **Success**: Clean demo page with working links
   ❌ **Failure**: White screen or broken links

### **Method 2: Command Line Health Check**
```cmd
# Check if services are running
netstat -ano | findstr "8083\|8080"

# Should show:
# TCP    0.0.0.0:8083    (User Service)
# TCP    0.0.0.0:8080    (API Gateway)
```

### **Method 3: API Testing with Swagger UI**

#### **Step 1: Register a New User**
1. Open Swagger UI: `http://localhost:8083/swagger-ui.html`
2. Find `POST /api/auth/register`
3. Click "Try it out"
4. Use this test data:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@test.com",
  "password": "Test123!",
  "phoneNumber": "+1234567890"
}
```
✅ **Success**: Returns 200/201 with success message
❌ **Failure**: Error response or timeout

#### **Step 2: Login with User**
1. Find `POST /api/auth/login`
2. Use these credentials:
```json
{
  "email": "john.doe@test.com",
  "password": "Test123!"
}
```
✅ **Success**: Returns JWT access and refresh tokens
❌ **Failure**: Invalid credentials or server error

#### **Step 3: Test Protected Endpoint**
1. Copy the `accessToken` from login response
2. Find `GET /api/users/profile`
3. Click "Authorize" button (🔒) in Swagger
4. Enter: `Bearer YOUR_ACCESS_TOKEN`
5. Execute the request

✅ **Success**: Returns user profile data
❌ **Failure**: Unauthorized or token invalid

### **Method 4: Database Verification**
1. Open H2 Console: `http://localhost:8083/h2-console`
2. Connect with:
   - **JDBC URL**: `jdbc:h2:mem:hotel_user_db`
   - **User**: `sa`
   - **Password**: (empty)
3. Run query:
```sql
SELECT * FROM USERS;
```
✅ **Success**: Shows registered users
❌ **Failure**: Empty table or connection error

### **Method 5: Service Status Check**
```cmd
# Check User Service
curl http://localhost:8083/swagger-ui.html

# Check API Gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

## 🚨 **Common Issues & Solutions**

### **Problem: Port Already in Use**
```cmd
# Find process using port
netstat -ano | findstr :8083

# Kill the process (replace PID)
taskkill /F /PID [PID_NUMBER]
```

### **Problem: Service Won't Start**
1. Check if PostgreSQL is running (we're using H2 instead)
2. Verify H2 profile is active
3. Check for compilation errors

### **Problem: Database Connection Failed**
- **JDBC URL**: Must be `jdbc:h2:mem:hotel_user_db`
- **User**: `sa`
- **Password**: Leave empty
- **Driver**: H2 (should auto-detect)

### **Problem: JWT Token Issues**
1. Register a new user first
2. Copy the EXACT token (including "Bearer " prefix)
3. Use Swagger's Authorize button
4. Token expires after 1 hour

## 🎯 **What Should Work Right Now**

### ✅ **Working Features**
- ✅ User registration
- ✅ User login with JWT
- ✅ Token-based authentication
- ✅ Password validation
- ✅ Email validation
- ✅ H2 database integration
- ✅ Swagger API documentation
- ✅ User profile management
- ✅ JWT refresh tokens

### ⚠️ **In Development**
- ⚠️ Room Service (compilation errors)
- ⚠️ Payment Service (not started)
- ⚠️ API Gateway routing to room/payment services

### 🔧 **Quick Demo Script**
1. "Here's our running User Service" → Show Swagger UI
2. "Let me register a new user" → Use register endpoint
3. "Now I'll login to get JWT tokens" → Use login endpoint
4. "Here's the real-time database" → Show H2 console
5. "All data is persistent during session" → Show user table

## 📊 **Expected Results**

### **User Registration Success**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "uuid-here",
    "email": "john.doe@test.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### **Login Success**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "uuid-here",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### **Database Query Result**
```
ID                                   | EMAIL              | FIRST_NAME | LAST_NAME
uuid-string-here                     | john.doe@test.com  | John       | Doe
```

---

## 🎉 **System is Working If:**
- ✅ Swagger UI loads completely
- ✅ User registration succeeds
- ✅ Login returns valid JWT tokens
- ✅ H2 console shows user data
- ✅ Protected endpoints work with JWT
- ✅ Database persists data during session

**Your hotel reservation system authentication module is fully functional!**
