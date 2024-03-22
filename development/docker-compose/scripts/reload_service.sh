#!/bin/bash

# Ensure service name is provided as argument
if [ -z "$1" ]; then
    echo "Please provide the service name as an argument."
    exit 1
fi

# Stop all containers for the service
echo "Stopping all containers for Service: $1"
docker-compose stop -t 1 $1

# Remove all containers for the service
echo "Removing all containers for Service: $1"
docker-compose rm -f $1

# Build the new service
echo "Building Service: $1"
docker-compose build $1

# Check dependent services without starting the target service
echo "Checking dependent services for: $1"
docker-compose up --no-start $1

# Start the new service
echo "Starting Service: $1"
docker-compose up -d $1
