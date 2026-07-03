# Build stage
FROM gradle:8.0-jdk17 AS builder

WORKDIR /app

# Copy project files
COPY . .

# Build the application with UTF-8 encoding
RUN gradle build -x test -Dfile.encoding=UTF-8 --info

# Runtime stage
FROM openjdk:17-slim

WORKDIR /app

# Set UTF-8 environment
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8" \
    SPRING_APPLICATION_JSON='{"spring.jpa.show-sql":false}'

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/autotrack-*.jar app.jar

# Expose ports
EXPOSE 8082 5055

# Run the application
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
