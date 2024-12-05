#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

# Ensure credentials are unset even if the script exits prematurely
trap 'unset ARTIFACTS_CREDS_USR; unset ARTIFACTS_CREDS_PSW' EXIT

# Load environment variables
source ../docker-compose/.env

# Prompt for Artifactory credentials
read -p "Enter your Artifactory 'Username' (you can take the 'User Profile' from https://repo.backbase.com/ui/user_profile): " artifacts_creds_usr
export ARTIFACTS_CREDS_USR="$artifacts_creds_usr"

read -sp "Enter your 'API Key' (you can take it from https://repo.backbase.com/ui/user_profile): " artifacts_creds_psw
export ARTIFACTS_CREDS_PSW="$artifacts_creds_psw"
echo

echo "Downloading Bootstrap job ..."
mvn clean process-resources -Dbootstrap.job.version="$BOOTSTRAP_JOB_VERSION" -s settings.xml
echo "Downloading Bootstrap job succeeded."

cd target/bootstrap-job
echo "Building Bootstrap job ..."
mvn clean install -s ../../settings.xml
echo "Building Bootstrap job succeeded."

echo "Building Bootstrap job docker image ..."
mvn -ntp -B package -s ../../settings.xml -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag \
    -Dskip.unit.tests=true -Dskip.integration.tests=true -Dimage=bootstrap-job \
    -Djib.to.tags="$BOOTSTRAP_JOB_VERSION"
echo "Building Bootstrap job docker image succeeded."

echo "Bootstrap job preparation completed successfully!"