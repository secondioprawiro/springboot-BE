FROM eclipse-temurin:21-jdk-alpine

LABEL maintainer="bootcamp-microservices"

VOLUME /tmp

# Tangkap file .jar hasil build
ARG JAR_FILE=target/*.jar

#Pindahkan file .jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]