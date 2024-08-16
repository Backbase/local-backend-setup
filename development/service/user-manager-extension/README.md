# To create docker image

``mvn clean package -Pdocker-image,no-latest-tag,local-client -Dmaven.test.skip=true``