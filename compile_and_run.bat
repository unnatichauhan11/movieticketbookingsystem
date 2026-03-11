@echo off
REM Movie Ticket Booking System - Quick Setup Script for Windows

echo.
echo ========================================
echo Movie Ticket Booking System - JSON Version
echo ========================================
echo.

REM Check if json.jar exists
if not exist "json-20231013.jar" (
    echo ERROR: json-20231013.jar not found!
    echo.
    echo Please download org.json library from:
    echo https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar
    echo.
    echo Save it in the current directory as: json-20231013.jar
    echo.
    pause
    exit /b 1
)

echo Step 1: Compiling MovieBookingSystem.java...
javac -cp json-20231013.jar MovieBookingSystem.java

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Step 2: Compilation successful!
echo.
echo Step 3: Running the application...
echo.

java -cp .;json-20231013.jar MovieBookingSystem

if %errorlevel% neq 0 (
    echo ERROR: Failed to run the application!
    pause
    exit /b 1
)
