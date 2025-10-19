# Multi-stage Dockerfile for WhatsApp Event Service (Spring Boot)
FROM gradle:8.5-jdk16-alpine as builder

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY build.gradle .
COPY settings.gradle .
COPY gradle/ gradle/
COPY gradlew .
COPY src/ src/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build -x test

# Runtime stage
FROM openjdk:16-jre-slim

# Install necessary packages
RUN apt-get update && \
    apt-get install -y curl wget && \
    rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/build/libs/*.jar app.jar

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV DB_URL="jdbc:mysql://mysql:3306/whatsapp_service"
ENV DB_USER="whatsapp_user"
ENV DB_PASSWORD="whatsapp_password"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
