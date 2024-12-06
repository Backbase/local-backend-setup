#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

# Load environment variables
source ../../docker-compose/.env

echo "Downloading Bootstrap job ..."
mvn clean process-resources -Dbootstrap.job.version="$BOOTSTRAP_JOB_VERSION"
echo "Downloading Bootstrap job succeeded."
