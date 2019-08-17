FROM openjdk:8-jdk-alpine

WORKDIR /application

COPY rancher-cloudflare/build/libs/rancher-cloudflare.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
