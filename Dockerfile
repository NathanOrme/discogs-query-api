# Stage 1: Build the application
FROM maven:3.9-amazoncorretto-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml .
COPY src ./src

# Package the application (This will compile the code and build the JAR)
RUN mvn clean package -DskipTests

# Stage 2: Create a smaller runtime image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/discogs-app.jar"]
