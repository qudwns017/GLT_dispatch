FROM openjdk:17-jdk-alpine

ARG JAR_FILE=build/libs/FinalProjectServer.jar

COPY ${JAR_FILE} /FinalProjectServer.jar

RUN apk update && apk add --no-cache ttf-dejavu

ENTRYPOINT  ["java", "-jar", "FinalProjectServer.jar"]
