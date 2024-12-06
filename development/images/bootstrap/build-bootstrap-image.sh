#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

# Load environment variables
source ../../docker-compose/.env

cd target/bootstrap-job
echo "Building Bootstrap job ..."
mvn clean install
echo "Building Bootstrap job succeeded."

echo "Building Bootstrap job docker image ..."
mvn -ntp -B package -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag \
    -Dskip.unit.tests=true -Dskip.integration.tests=true -Dimage=bootstrap-job \
    -Djib.to.tags="$BOOTSTRAP_JOB_VERSION"
echo "Building Bootstrap job docker image succeeded."
