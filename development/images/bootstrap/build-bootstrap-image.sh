#!/bin/sh
set -e  # Exit immediately if a command exits with a non-zero status

# Load environment variables
source ../../docker-compose/.env

cd target/bootstrap-job
PLUGIN_EXCLUSION="<build>\n    <plugins>\n        <plugin>\n            <groupId>org.codehaus.gmavenplus</groupId>\n            <artifactId>gmavenplus-plugin</artifactId>\n            <executions>\n                <execution>\n                    <id>starter</id>\n                    <phase>none</phase> <!-- Disable execution entirely -->\n                </execution>\n            </executions>\n        </plugin>\n    </plugins>\n</build>"

awk '
/<\/project>/ {
    print plugin_exclusion
    print $0
    next
}
{ print $0 }
' plugin_exclusion="$PLUGIN_EXCLUSION" pom.xml > pom.xml.tmp && mv pom.xml.tmp pom.xml
echo "Building Bootstrap job ..."
mvn clean install
echo "Building Bootstrap job succeeded."

echo "Building Bootstrap job docker image ..."
mvn -ntp -B package -pl :bootstrap-job -Pdocker-image,local-client,no-latest-tag \
    -Dskip.unit.tests=true -Dskip.integration.tests=true -Dimage=bootstrap-job \
    -Djib.to.tags="$BOOTSTRAP_JOB_VERSION"
echo "Building Bootstrap job docker image succeeded."
