# Stage 1: Build the application
FROM maven:3-amazoncorretto-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven POM file and source code
COPY backend/pom.xml ./
COPY backend/src ./src

# Package the application (This will compile the code and build the JAR)
RUN mvn clean package -DskipTests

# Stage 2: Create a smaller runtime image
FROM openjdk:24-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Expose the port the application will run on
EXPOSE 8080

# Add metadata labels
LABEL org.opencontainers.image.title="Discogs Query Application" \
      org.opencontainers.image.version="1.0" \
      org.opencontainers.image.description="A Java application for querying Discogs" \
      org.opencontainers.image.authors="Nathan Orme"

# Health check to ensure the application is running properly
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD ["curl", "--silent", "--fail", "http://localhost:8080/actuator/health"]

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/discogs-app.jar"]
