FROM eclipse-temurin:21-jdk-alpine

WORKDIR /gym

COPY target/demo-0.0.1-SNAPSHOT.jar gym.jar

EXPOSE 8081

CMD ["java", "-jar", "gym.jar"]
