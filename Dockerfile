# Stage 1: Build the frontend
FROM node:22 AS frontend-builder
WORKDIR /app/frontend

# Copy package.json and install dependencies
COPY frontend/package.json ./
COPY frontend/package-lock.json ./
RUN npm install

# Install serve globally
RUN npm install -g serve

# Copy the rest of the frontend source code
COPY frontend/src ./src
COPY frontend/public ./public

# Build the frontend
RUN npm run build

# Stage 2: Build the backend
FROM maven:3-amazoncorretto-21 AS backend-builder
WORKDIR /app/backend

# Copy the Maven POM file and source code for the backend
COPY backend/pom.xml ./
COPY backend/src ./src

# Package the backend application (This will compile the code and build the JAR)
RUN mvn clean package -DskipTests

# Stage 3: Create a runtime image with Node.js and OpenJDK
FROM node:22 AS runtime
WORKDIR /app

# Install OpenJDK
RUN apt-get update && apt-get install -y openjdk-21-jdk && apt-get clean

# Install Serve globally
RUN npm install -g serve

# Copy the built JAR file from the backend builder stage
COPY --from=backend-builder /app/backend/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar

# Copy the frontend build files
COPY --from=frontend-builder /app/frontend/build ./frontend/build

# Expose the ports
EXPOSE 9090 3000

# Add metadata labels
LABEL org.opencontainers.image.title="Discogs Query Application" \
      org.opencontainers.image.version="1.0" \
      org.opencontainers.image.description="A Java application for querying Discogs" \
      org.opencontainers.image.authors="Nathan Orme"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD ["curl", "--silent", "--fail", "http://localhost:9090/actuator/health"]

# Run both the backend and frontend
ENTRYPOINT ["sh", "-c", "java -jar /app/discogs-app.jar & serve -s frontend/build -l 3000"]
