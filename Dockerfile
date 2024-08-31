# Use a base image that won't interfere with buildpacks
FROM openjdk:17-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file (if it’s available) into the image
COPY target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/discogs-app.jar"]