@echo off
echo ===============================================
echo Hotel Reservation System - Quick Start Guide
echo ===============================================
echo.

echo This script will help you set up and run the complete hotel reservation system.
echo.

echo Prerequisites Check:
echo.

REM Check Java
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo Please install Java 17 or higher
    goto :end
) else (
    echo ✅ Java is installed
)

REM Check Maven
mvn -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ Maven is not installed or not in PATH
    echo Please install Apache Maven
    goto :end
) else (
    echo ✅ Maven is installed
)

REM Check PostgreSQL
pg_isready -h localhost -p 5432 >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ PostgreSQL is not running
    echo Please start PostgreSQL service
    goto :end
) else (
    echo ✅ PostgreSQL is running
)

echo.
echo All prerequisites are met!
echo.

echo ===============================================
echo Setup Options:
echo ===============================================
echo.
echo 1. First-time setup (Database + Build + Run)
echo 2. Build and run (if databases already exist)
echo 3. Just run services (if already built)
echo 4. Stop all services
echo 5. Exit
echo.

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto :first_time
if "%choice%"=="2" goto :build_run
if "%choice%"=="3" goto :just_run
if "%choice%"=="4" goto :stop_services
if "%choice%"=="5" goto :end

echo Invalid choice!
goto :end

:first_time
echo.
echo ===============================================
echo FIRST-TIME SETUP
echo ===============================================
echo.
echo Step 1: Setting up databases...
call setup-databases.bat
echo.
echo Step 2: Building project...
call build-project.bat
echo.
echo Step 3: Starting services...
call run-services.bat
goto :end

:build_run
echo.
echo ===============================================
echo BUILD AND RUN
echo ===============================================
echo.
echo Step 1: Building project...
call build-project.bat
echo.
echo Step 2: Starting services...
call run-services.bat
goto :end

:just_run
echo.
echo ===============================================
echo STARTING SERVICES
echo ===============================================
call run-services.bat
goto :end

:stop_services
echo.
echo ===============================================
echo STOPPING SERVICES
echo ===============================================
call stop-services.bat
goto :end

:end
echo.
pause
