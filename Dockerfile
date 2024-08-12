FROM openjdk:17-jdk-alpine

ARG JAR_FILE=build/libs/FinalProjectServer.jar

COPY ${JAR_FILE} /FinalProjectServer.jar

ENTRYPOINT  ["java", "-jar", "FinalProjectServer.jar"]