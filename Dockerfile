# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml .
COPY src ./src

# Package the application (This will compile the code and build the JAR)
RUN mvn clean package -DskipTests

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/your-app.jar"]

# Use a base image that won't interfere with buildpacks
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file (if it’s available) into the image
COPY --from=builder target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Expose the port the application will run on
EXPOSE 9090

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/discogs-app.jar"]