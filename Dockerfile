FROM openjdk:17-jdk-alpine

ARG JAR_FILE=build/libs/FinalProjectServer.jar

COPY ${JAR_FILE} /FinalProjectServer.jar

ENTRYPOINT  ["java", "-jar", "FinalProjectServer.jar"]

RUN apt-get update && apt-get install -y fonts-dejavu