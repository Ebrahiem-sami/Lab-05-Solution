FROM eclipse-temurin:25.0.2_10-jdk

WORKDIR /app

COPY target/*.jar app.jar

ENV USER_NAME=Docker_Ibrahim_Sami
ENV ID=55-12509

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
