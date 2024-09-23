# Stage 1: Build the application
FROM maven:3-amazoncorretto-21 AS builder
# Set the working directory for the backend
WORKDIR /app/backend

# Copy the Maven POM file and source code for the backend
COPY backend/pom.xml ./ 
COPY backend/src ./src 

# Package the backend application (This will compile the code and build the JAR)
RUN mvn clean package -DskipTests

# Stage 2: Build the frontend
FROM node:22 AS frontend-builder
# Set the working directory for the frontend
WORKDIR /app/frontend

# Copy package.json and install dependencies
COPY frontend/package.json ./ 
COPY frontend/package-lock.json ./ 
RUN npm install

# Copy the rest of the frontend source code
COPY frontend/src ./src
COPY frontend/public ./public

# Build the frontend
RUN npm run build

# Stage 3: Create a runtime image with both Java and Node.js
FROM amazoncorretto:21 AS runtime
# Set the working directory
WORKDIR /app

# Copy the built JAR file from the backend builder stage
COPY --from=builder /app/backend/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Copy the frontend build files
COPY --from=frontend-builder /app/frontend/build ./frontend/build

# Install Node.js and serve
RUN apt-get update && apt-get install -y nodejs npm && npm install -g serve

# Expose the ports
EXPOSE 9090 3000

# Add metadata labels
LABEL org.opencontainers.image.title="Discogs Query Application" \
      org.opencontainers.image.version="1.0" \
      org.opencontainers.image.description="A Java application for querying Discogs" \
      org.opencontainers.image.authors="Nathan Orme"

# Health check to ensure the application is running properly
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD ["curl", "--silent", "--fail", "http://localhost:9090/actuator/health"]

# Run both the backend and frontend
ENTRYPOINT ["sh", "-c", "java -jar /app/discogs-app.jar & serve -s frontend/build -l 3000"]
