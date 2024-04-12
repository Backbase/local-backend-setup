#!/bin/bash

# Find the container ID
CONTAINER_ID=$(docker ps -q --filter "name=access-control")

# Check if the container is running
if [ -n "$CONTAINER_ID" ]; then
    # If running, kill the container with SIGKILL
    docker kill -s KILL "$CONTAINER_ID"
    echo "Container 'access-control' (ID: $CONTAINER_ID) killed."
else
    echo "Container 'access-control' is not running."
fi
