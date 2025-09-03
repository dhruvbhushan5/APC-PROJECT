@echo off
title Hotel Reservation System - Complete Startup with Webpack Dev Server
color 0A
echo.
echo ===============================================
echo    🏨 Hotel Reservation System - Full Stack
echo ===============================================
echo.

echo [INFO] Starting complete hotel reservation system with all services...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java and try again.
    pause
    exit /b 1
)

REM Check if Node.js is available
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed or not in PATH
    echo Please install Node.js and try again.
    pause
    exit /b 1
)

echo [✓] Java detected
echo [✓] Node.js detected
echo.

REM Kill any existing services on our ports
echo [INFO] Cleaning up existing services on ports 8080-8083 and 3000...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    echo [INFO] Stopping process on port 8080...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8081') do (
    echo [INFO] Stopping process on port 8081...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8082') do (
    echo [INFO] Stopping process on port 8082...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083') do (
    echo [INFO] Stopping process on port 8083...
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000') do (
    echo [INFO] Stopping process on port 3000...
    taskkill /F /PID %%a >nul 2>&1
)

timeout /t 3 /nobreak >nul
echo [✓] Ports cleared
echo.

REM Build the Maven projects
echo [INFO] Building the Maven projects...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo [ERROR] Maven build failed
    pause
    exit /b 1
)
echo [✓] Maven projects built successfully
echo.

REM Install Frontend Dependencies
echo [INFO] Installing frontend dependencies...
cd frontend
call npm install >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] npm install had issues, but continuing...
)
cd ..
echo [✓] Frontend dependencies ready
echo.

echo ===============================================
echo           🚀 Starting All Services
echo ===============================================
echo.

REM Start User Service
echo [1/5] Starting User Service (Port 8083)...
start "User Service" cmd /c "cd user-service && echo Starting User Service... && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul

REM Start Room Service
echo [2/5] Starting Room Service (Port 8081)...
start "Room Service" cmd /c "cd room-service && echo Starting Room Service... && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul

REM Start Payment Service
echo [3/5] Starting Payment Service (Port 8082)...
start "Payment Service" cmd /c "cd payment-service && echo Starting Payment Service... && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul

REM Start API Gateway
echo [4/5] Starting API Gateway (Port 8080)...
start "API Gateway" cmd /c "cd api-gateway && echo Starting API Gateway... && mvn spring-boot:run"
timeout /t 8 /nobreak >nul

REM Start Frontend Webpack Dev Server
echo [5/5] Starting Frontend Webpack Dev Server (Port 3000)...
start "Frontend Webpack Server" cmd /c "cd frontend && echo Starting Webpack Dev Server... && npm start"
timeout /t 5 /nobreak >nul

echo.
echo ===============================================
echo           🎉 ALL SERVICES STARTED
echo ===============================================
echo.
echo 📊 Service Status:
echo   User Service:     http://localhost:8083
echo   Room Service:     http://localhost:8081
echo   Payment Service:  http://localhost:8082
echo   API Gateway:      http://localhost:8080  
echo   Frontend:         http://localhost:3000 (Webpack Dev Server)
echo.
echo 🔗 Quick Access Links:
echo   Main App:         http://localhost:3000
echo   User API Docs:    http://localhost:8083/swagger-ui.html
echo   Room API Docs:    http://localhost:8081/swagger-ui.html
echo   Payment API Docs: http://localhost:8082/swagger-ui.html
echo   Gateway Health:   http://localhost:8080/actuator/health
echo.
echo 🗄️  H2 Database Consoles:
echo   User DB:          http://localhost:8083/h2-console
echo   Room DB:          http://localhost:8081/h2-console
echo   Payment DB:       http://localhost:8082/h2-console
echo.
echo 🔑 Default H2 Database Settings:
echo   JDBC URL:         jdbc:h2:mem:hotel_*_db
echo   Username:         sa
echo   Password:         (leave empty)
echo.

REM Wait for services to start
echo [INFO] Waiting for all services to initialize (this may take 60-90 seconds)...
echo.

:wait_loop
timeout /t 5 /nobreak >nul

REM Check User Service
curl -s http://localhost:8083/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] User Service still starting...
    goto wait_loop
)

REM Check Room Service
curl -s http://localhost:8081/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Room Service still starting...
    goto wait_loop
)

REM Check API Gateway
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] API Gateway still starting...
    goto wait_loop
)

REM Check Frontend
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% neq 0 (
    echo [INFO] Frontend still starting...
    goto wait_loop
)

echo [✓] All services are ready!
echo.

REM Open the application in browser
echo [INFO] Opening application in browser...
timeout /t 2 /nobreak >nul
start http://localhost:3000
timeout /t 1 /nobreak >nul
start http://localhost:8081/swagger-ui.html

echo.
echo ===============================================
echo     🚀 Hotel Reservation System is LIVE!
echo ===============================================
echo.
echo 💡 Tips:
echo   - Frontend has hot-reload enabled for development
echo   - Use Ctrl+C in service terminals to stop individual services
echo   - Run stop-services.bat to stop all services at once
echo.
echo Press any key to view system status or Ctrl+C to exit...
pause >nul

REM Show system status
:status_loop
cls
echo ===============================================
echo        🏨 Hotel Reservation System Status
echo ===============================================
echo.
echo 📊 Service Health Check:
echo.

REM Check User Service
curl -s http://localhost:8083/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   User Service:     ✅ RUNNING  (Port 8083)
) else (
    echo   User Service:     ❌ STOPPED  (Port 8083)
)

REM Check Room Service
curl -s http://localhost:8081/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   Room Service:     ✅ RUNNING  (Port 8081)
) else (
    echo   Room Service:     ❌ STOPPED  (Port 8081)
)

REM Check Payment Service
curl -s http://localhost:8082/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   Payment Service:  ✅ RUNNING  (Port 8082)
) else (
    echo   Payment Service:  ❌ STOPPED  (Port 8082)
)

REM Check API Gateway
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo   API Gateway:      ✅ RUNNING  (Port 8080)
) else (
    echo   API Gateway:      ❌ STOPPED  (Port 8080)
)

REM Check Frontend
curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo   Frontend:         ✅ RUNNING  (Port 3000) - Webpack Dev Server
) else (
    echo   Frontend:         ❌ STOPPED  (Port 3000)
)

echo.
echo 🔗 Application URLs:
echo   Main App:         http://localhost:3000
echo   Room API:         http://localhost:8081/swagger-ui.html
echo   User API:         http://localhost:8083/swagger-ui.html
echo   Payment API:      http://localhost:8082/swagger-ui.html
echo.
echo 📊 Port Usage:
netstat -ano | findstr ":8080\|:8081\|:8082\|:8083\|:3000" | findstr LISTENING

echo.
echo [R] Restart Services  [S] Stop All  [O] Open Apps  [T] Test APIs  [Q] Quit
choice /c RSOTQ /n /m "Choose option: "

if %errorlevel% equ 1 goto restart_services
if %errorlevel% equ 2 goto stop_all
if %errorlevel% equ 3 goto open_apps
if %errorlevel% equ 4 goto test_apis
if %errorlevel% equ 5 goto exit_script

:restart_services
echo.
echo [INFO] Restarting all services...
REM Kill existing services
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080\|:8081\|:8082\|:8083\|:3000"') do (
    taskkill /F /PID %%a >nul 2>&1
)
timeout /t 5 /nobreak >nul

REM Restart all services
start "User Service" cmd /c "cd user-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul
start "Room Service" cmd /c "cd room-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul
start "Payment Service" cmd /c "cd payment-service && mvn spring-boot:run -Dspring-boot.run.profiles=h2"
timeout /t 8 /nobreak >nul
start "API Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"
timeout /t 8 /nobreak >nul
start "Frontend Webpack Server" cmd /c "cd frontend && npm start"
echo [✓] All services restarted
timeout /t 10 /nobreak >nul
goto status_loop

:stop_all
echo.
echo [INFO] Stopping all services...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080\|:8081\|:8082\|:8083\|:3000"') do (
    echo [INFO] Stopping PID %%a...
    taskkill /F /PID %%a >nul 2>&1
)
echo [✓] All services stopped
pause
goto exit_script

:open_apps
echo [INFO] Opening all application URLs...
start http://localhost:3000
timeout /t 1 /nobreak >nul
start http://localhost:8081/swagger-ui.html
timeout /t 1 /nobreak >nul
start http://localhost:8083/swagger-ui.html
timeout /t 1 /nobreak >nul
start http://localhost:8082/swagger-ui.html
timeout /t 1 /nobreak >nul
start http://localhost:8080/actuator/health
goto status_loop

:test_apis
echo.
echo [INFO] Testing API endpoints...
echo.
echo Testing Room Service API:
curl -s http://localhost:8081/api/rooms | echo Room API: %errorlevel%
echo.
echo Testing User Service API:
curl -s http://localhost:8083/actuator/health | echo User API: %errorlevel%
echo.
echo Testing Payment Service API:
curl -s http://localhost:8082/actuator/health | echo Payment API: %errorlevel%
echo.
echo Testing API Gateway:
curl -s http://localhost:8080/actuator/health | echo Gateway: %errorlevel%
echo.
pause
goto status_loop

:exit_script
echo.
echo [INFO] Shutting down Hotel Reservation System...
echo.
echo 🔄 To stop all services later, run: stop-services.bat
echo 🚀 To restart system, run: start-all-services.bat
echo.
echo Thank you for using our Hotel Reservation System!
echo.
exit /b 0
