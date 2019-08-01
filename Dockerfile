FROM openjdk:8-jdk-alpine

ARG JAR_FILE

WORKDIR /application

COPY $JAR_FILE app.jar

ENTRYPOINT ["java", "-Xmx32m -Xss256k", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
