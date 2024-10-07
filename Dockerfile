# Stage 1: Build the frontend
FROM node:22 AS frontend-builder

# Set the working directory for the frontend build
WORKDIR /app/frontend

# Copy only the package.json and package-lock.json first for efficient caching
COPY src/main/frontend/package.json ./
COPY src/main/frontend/package-lock.json ./

# Install frontend dependencies (using npm ci for consistency)
RUN npm ci && npm install -g serve

# Copy the rest of the frontend source code
COPY src/main/frontend/ ./

# Build the frontend application
RUN npm run build


# Stage 2: Build the backend
FROM maven:3-amazoncorretto-21 AS backend-builder

# Set the working directory for the backend build
WORKDIR /app/backend

# Copy only the POM file first for efficient caching of dependencies
COPY pom.xml ./

# Cache Maven dependencies
RUN mvn dependency:go-offline

# Copy the backend source code
COPY src ./src

# Package the backend application, skipping tests for faster build time
RUN mvn clean package -DskipTests


# Stage 3: Create a lightweight runtime image
FROM node:22-alpine AS runtime

# Set the working directory for the runtime environment
WORKDIR /app

# Install OpenJDK and serve globally
RUN apk add --no-cache openjdk21 && npm install -g serve

# Copy the built JAR file from the backend builder stage
COPY --from=backend-builder /app/backend/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Copy the built frontend files
COPY --from=frontend-builder /app/frontend/build ./frontend/build

# Add metadata labels for the image
LABEL org.opencontainers.image.title="Discogs Query Application" \
      org.opencontainers.image.version="1.0" \
      org.opencontainers.image.description="A Java application for querying Discogs" \
      org.opencontainers.image.authors="Nathan Orme"

# Expose ports for frontend and backend
EXPOSE 3000 9090

# Configure health check for the application
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD curl --silent --fail http://localhost:9090/actuator/health || exit 1

# Set the entry point to run both backend and frontend
ENTRYPOINT ["sh", "-c", "java -jar /app/discogs-app.jar & serve -s frontend/build -l 3000"]
