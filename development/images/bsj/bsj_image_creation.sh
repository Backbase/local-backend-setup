#!/bin/bash

# URL of the ZIP file
ZIP_URL="https://repo.backbase.com/ui/native/repo/com/backbase/accelerators/bootstrap-job/bootstrap-job-2024.10-retail-src.zip"

# Directory to download and extract the ZIP file
DOWNLOAD_DIR="./BSJDownload"

# Destination directory for extracted files
EXTRACT_DIR="$DOWNLOAD_DIR/extracted"

# Ensure the download directory exists
mkdir -p "$DOWNLOAD_DIR"

# Download the ZIP file
echo "Downloading ZIP file from $ZIP_URL..."
curl -L "$ZIP_URL" -o "$DOWNLOAD_DIR/file.zip"

# Check if the download was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to download the ZIP file."
    exit 1
fi

# Extract the ZIP file
echo "Extracting ZIP file to $EXTRACT_DIR..."
mkdir -p "$EXTRACT_DIR"
unzip "$DOWNLOAD_DIR/file.zip" -d "$EXTRACT_DIR"

# Check if the extraction was successful
if [ $? -ne 0 ]; then
    echo "Error: Failed to extract the ZIP file."
    exit 1
fi

# Navigate to the extracted directory
cd "$EXTRACT_DIR" || exit

# Build the project using Maven
echo "Building the project with Maven..."
mvn -ntp -B package -pl :bootstrap-job -Pdocker-image -Pno-latest-tag -Dskip.unit.tests=true -Dskip.integration.tests=true
-Ddocker.image.name=bootstrap-job
-Ddocker.default.tag=test-2.0.0

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Docker image creation successful!"
else
    echo "Error: Docker image creation failed."
fi

# Clean up Delete the extracted folder and zip file
cd ../../
rm -r $DOWNLOAD_DIR

echo "End of Script"

exit 1