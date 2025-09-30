# BUILD stage (using Maven + OpenJDK 17)
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Optional: copy dependency declaration and download dependencies first for caching
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the project (skip tests to speed up; remove -DskipTests if you want tests)
RUN mvn clean package -DskipTests

# RUNTIME stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# (Optional) create non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

# Copy the jar from build stage; adjust path if your jar name is different
COPY --from=build /app/target/*.jar app.jar

# Expose port inside container
EXPOSE 8080

# Command to run
ENTRYPOINT ["java","-jar","/app/app.jar"]
