# Build the fat JAR inside Docker so `docker compose up --build` works without a local `mvn package`.
FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:25.0.2_10-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV USER_NAME=Docker_Ibrahim_Sami
ENV ID=55-12509

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
