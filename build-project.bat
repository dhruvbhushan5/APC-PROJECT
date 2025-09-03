@echo off
echo ===============================================
echo Hotel Reservation System - Build Project
echo ===============================================
echo.

echo Building the entire project...
echo This may take a few minutes on first run...
echo.

mvn clean install -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ===============================================
    echo BUILD FAILED!
    echo ===============================================
    echo Please check the error messages above.
    echo Common issues:
    echo 1. Java not installed or not in PATH
    echo 2. Maven not installed or not in PATH
    echo 3. Network issues downloading dependencies
    echo.
    pause
    exit /b 1
)

echo.
echo ===============================================
echo BUILD SUCCESSFUL!
echo ===============================================
echo.
echo Running tests...
mvn test

if %ERRORLEVEL% neq 0 (
    echo.
    echo ===============================================
    echo SOME TESTS FAILED!
    echo ===============================================
    echo The build was successful but some tests failed.
    echo You can still run the services, but check the test output above.
    echo.
) else (
    echo.
    echo ===============================================
    echo ALL TESTS PASSED!
    echo ===============================================
    echo.
)

echo Project is ready to run!
echo Use 'run-services.bat' to start all services.
echo.
pause
