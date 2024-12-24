# Stage 1: Build the Spring Boot fat JAR
FROM openjdk:17.0.2-jdk-slim AS builder
WORKDIR /app

# Copy Gradle-related files
COPY gradle gradle/
COPY *.gradle.kts gradlew build.gradle ./

# Pre-fetch dependencies
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Copy the application source
COPY src src/

# Build the Spring Boot fat JAR
RUN ./gradlew clean bootJar -x test

# Stage 2: Run the fat JAR
FROM gcr.io/distroless/java17-debian11:nonroot

# Expose the application port
EXPOSE 8080
WORKDIR /app

# Copy the Spring Boot fat JAR explicitly
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# Copy the .env file
COPY ../.env ./.env

# Run the Spring Boot fat JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
