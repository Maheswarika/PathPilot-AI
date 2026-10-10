# Multi-stage Dockerfile for PathPilot AI Spring Boot Backend
# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace/app

# Copy maven wrapper / pom.xml and dependencies
COPY pom.xml .
COPY src ./src

# Build production jar
RUN ./mvnw clean package -DskipTests 2>/dev/null || (apt-get update && apt-get install -y maven && mvn clean package -DskipTests)

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root group and user
RUN groupadd -r spring && useradd -r -g spring spring

# Copy jar from builder
COPY --from=builder /workspace/app/target/*.jar app.jar

# Set ownership
RUN chown -R spring:spring /app
USER spring:spring

# Expose default port (Render will override via PORT environment variable)
EXPOSE 8081

# Launch application with production container flags
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8081} -jar app.jar"]
