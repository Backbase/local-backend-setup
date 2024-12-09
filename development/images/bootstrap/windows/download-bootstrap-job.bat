@echo off
REM Exit immediately if a command exits with a non-zero status
setlocal enabledelayedexpansion
set "errorlevel="
if errorlevel 1 exit /b 1

REM Load environment variables
FOR /F "tokens=* USEBACKQ" %%F IN ("..\..\..\docker-compose\.env") DO set %%F

echo Downloading Bootstrap job ...
mvn clean process-resources -f "..\pom.xml" -Dbootstrap.job.version="%BOOTSTRAP_JOB_VERSION%"
if errorlevel 1 (
    echo Downloading Bootstrap job failed.
    exit /b 1
) else (
    echo Downloading Bootstrap job succeeded.
)