@echo off
REM Exit immediately if a command exits with a non-zero status
setlocal enabledelayedexpansion
set "errorlevel="
if errorlevel 1 exit /b 1

REM Run the download-bootstrap-job script
call download-bootstrap-job.bat
if errorlevel 1 (
    echo "Failed to download Bootstrap job."
    exit /b 1
)

REM Run the build-bootstrap-image script
call build-bootstrap-image.bat
if errorlevel 1 (
    echo "Failed to build Bootstrap job image."
    exit /b 1
)

echo Bootstrap job preparation completed successfully!