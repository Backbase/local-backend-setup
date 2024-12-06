#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

./download-bootstrap-job.sh

./build-bootstrap-image.sh

echo "Bootstrap job preparation completed successfully!"