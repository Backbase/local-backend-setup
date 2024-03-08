# contact-manager extension
[Example Contact Manager Behaviour extensions from Community](https://community.backbase.com/documentation/DBS/latest/contacts_extend_behavior)

Build with JDK 17.

## Description
This example shows you how to modify the default behavior of the Contacts presentation service. 
This modification checks for specific country values and raises a flag if, for example, a country is on a blacklist.

## How to use
To use your service extension, you have to include the JAR build from this project to the CLASSPATH of the service.

[When you run a service as a bootable jar](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#executable-jar-property-launcher-features), 
you can use the `loader.path` command line argument to add JARs to the CLASSPATH. 
`loader.path` takes a comma-separated list of locations, which can reference JARs or 
directories containing one or more JARs. For example: 

    java -Dloader.path=/lib,/path/to/my.jar -jar myservice-boot.jar
    
If you are not running the Service as a war, use the mechanism available in your application server.

### Docker
By adding the profile `docker-image` to the build command, the project builds and pushes a new docker image
that already contains the extension:


    mvn clean package -Pdocker-image

Add the `local-client` profile to skip the image push to the remote registry.


To use the extension in other scenarios, please check the community documentation.