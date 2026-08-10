# Step 1: Build stage with Maven & Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy backend and frontend source directories
COPY backend ./backend
COPY frontend ./frontend

# Change working directory to backend and package application JAR
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Step 2: Runtime stage using JDK 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled executable JAR from build stage
COPY --from=build /app/backend/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
