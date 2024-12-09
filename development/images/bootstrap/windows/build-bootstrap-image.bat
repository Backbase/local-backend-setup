@echo off
REM Exit immediately if a command exits with a non-zero status
setlocal enabledelayedexpansion
set "errorlevel="
if errorlevel 1 exit /b 1

REM Load environment variables
FOR /F "tokens=* USEBACKQ" %%F IN ("..\..\..\docker-compose\.env") DO set %%F

cd target\bootstrap-job || (
    echo "Failed to navigate to target\bootstrap-job"
    exit /b 1
)

echo Building Bootstrap job ...
mvn clean install -f "..\pom.xml"
if errorlevel 1 (
    echo Building Bootstrap job failed.
    exit /b 1
) else (
    echo Building Bootstrap job succeeded.
)

echo Building Bootstrap job docker image ...
mvn -ntp -B package -f "..\pom.xml" -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag ^
    -Dskip.unit.tests=true -Dskip.integration.tests=true -Dimage=bootstrap-job ^
    -Djib.to.tags="%BOOTSTRAP_JOB_VERSION%"
if errorlevel 1 (
    echo Building Bootstrap job docker image failed.
    exit /b 1
) else (
    echo Building Bootstrap job docker image succeeded.
)