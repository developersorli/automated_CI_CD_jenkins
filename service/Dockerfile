FROM openjdk:latest
ADD target/spring-boot-test.jar spring-boot-test.jar
EXPOSE 9080 5005
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","spring-boot-test.jar"]