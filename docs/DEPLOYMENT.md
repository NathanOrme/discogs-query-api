# Deployment Guide

## Overview

The Discogs Query API supports multiple deployment strategies including containerized deployment with Docker, traditional server deployment, and cloud platform deployment. This guide covers all deployment options with detailed configuration and best practices.

## Prerequisites

### System Requirements

- **Java**: Amazon Corretto 24 or OpenJDK 24+
- **Node.js**: Version 20 or higher with Yarn
- **Memory**: Minimum 2GB RAM (4GB recommended for production)
- **Storage**: Minimum 1GB available disk space
- **Network**: Outbound HTTPS access to api.discogs.com

### Required Environment Variables

```bash
DISCOGS_TOKEN=your_discogs_api_token      # Required: Discogs API authentication
DISCOGS_AGENT=your_application_name       # Required: User agent for API requests
SERVER_PORT=9090                          # Optional: Server port (default: 9090)
ALLOWED_ORIGINS=https://yourdomain.com    # Optional: CORS allowed origins
```

## Docker Deployment (Recommended)

### Multi-Stage Docker Build

The application uses a sophisticated multi-stage Docker build process for optimal production images:

```dockerfile
# Stage 1: Frontend Build (Node 24 Alpine)
FROM node:24-alpine AS frontend-builder
WORKDIR /app/frontend
COPY src/main/frontend/package*.json ./
RUN yarn install --frozen-lockfile
COPY src/main/frontend/ ./
RUN yarn run build

# Stage 2: Backend Build (Maven + Amazon Corretto 24)
FROM maven:3-amazoncorretto-24 AS backend-builder
WORKDIR /app/backend
COPY pom.xml ./
RUN mvn dependency:go-offline --no-transfer-progress
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# Stage 3: Runtime (Amazon Corretto 24)
FROM amazoncorretto:24 AS runtime
WORKDIR /app
# Install Node.js for serving frontend assets
RUN yum update -y && \
    yum install -y curl && \
    curl -fsSL https://rpm.nodesource.com/setup_23.x | bash - && \
    yum install -y nodejs && \
    npm install -g yarn serve && \
    yum clean all

# Security: Create non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
USER appuser

# Copy built artifacts
COPY --from=backend-builder /app/backend/target/discogs-query-1.0-SNAPSHOT.jar /app/discogs-app.jar
COPY --from=frontend-builder /app/frontend/build ./frontend/build

EXPOSE 9090
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s \
  CMD wget --quiet --spider http://localhost:9090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/discogs-app.jar"]
```

### Building the Docker Image

```bash
# Build the image
docker build -t discogs-query-api:latest .

# Build with specific tag
docker build -t discogs-query-api:v1.0.0 .

# Build with build arguments
docker build \
  --build-arg JAVA_VERSION=24 \
  --build-arg NODE_VERSION=20 \
  -t discogs-query-api:latest .
```

### Running with Docker

#### Basic Deployment

```bash
docker run -d \
  --name discogs-query-api \
  -p 9090:9090 \
  -e DISCOGS_TOKEN=your_token_here \
  -e DISCOGS_AGENT=your_app_name \
  discogs-query-api:latest
```

#### Production Deployment

```bash
docker run -d \
  --name discogs-query-api \
  --restart unless-stopped \
  -p 9090:9090 \
  -e DISCOGS_TOKEN=your_token_here \
  -e DISCOGS_AGENT=your_app_name \
  -e SERVER_PORT=9090 \
  -e ALLOWED_ORIGINS=https://yourdomain.com \
  -e JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC" \
  --memory=3g \
  --cpus=2 \
  --health-cmd="wget --quiet --spider http://localhost:9090/actuator/health || exit 1" \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  discogs-query-api:latest
```

#### Using Environment File

```bash
# Create .env file
cat > .env << EOF
DISCOGS_TOKEN=your_token_here
DISCOGS_AGENT=your_app_name
SERVER_PORT=9090
ALLOWED_ORIGINS=https://yourdomain.com
JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC
EOF

# Run with environment file
docker run -d \
  --name discogs-query-api \
  --restart unless-stopped \
  -p 9090:9090 \
  --env-file .env \
  --memory=3g \
  --cpus=2 \
  discogs-query-api:latest
```

## Docker Compose Deployment

### Basic Docker Compose

```yaml
# docker-compose.yml
version: "3.8"

services:
  discogs-api:
    build: .
    ports:
      - "9090:9090"
    environment:
      - DISCOGS_TOKEN=${DISCOGS_TOKEN}
      - DISCOGS_AGENT=${DISCOGS_AGENT}
      - SERVER_PORT=9090
    healthcheck:
      test:
        [
          "CMD",
          "wget",
          "--quiet",
          "--spider",
          "http://localhost:9090/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
    restart: unless-stopped
    deploy:
      resources:
        limits:
          memory: 3G
          cpus: "2"
        reservations:
          memory: 1G
          cpus: "1"
```

### Production Docker Compose with Monitoring

```yaml
version: "3.8"

services:
  discogs-api:
    build: .
    ports:
      - "9090:9090"
    environment:
      - DISCOGS_TOKEN=${DISCOGS_TOKEN}
      - DISCOGS_AGENT=${DISCOGS_AGENT}
      - SERVER_PORT=9090
      - ALLOWED_ORIGINS=${ALLOWED_ORIGINS:-*}
      - JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError
    volumes:
      - ./logs:/app/logs
      - ./heapdumps:/app/heapdumps
    healthcheck:
      test:
        [
          "CMD",
          "wget",
          "--quiet",
          "--spider",
          "http://localhost:9090/actuator/health",
        ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
    restart: unless-stopped
    networks:
      - discogs-network
    deploy:
      resources:
        limits:
          memory: 3G
          cpus: "2"
        reservations:
          memory: 1G
          cpus: "1"
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - discogs-api
    networks:
      - discogs-network
    restart: unless-stopped

networks:
  discogs-network:
    driver: bridge
```

### Running with Docker Compose

```bash
# Start services
docker-compose up -d

# Start with build
docker-compose up -d --build

# View logs
docker-compose logs -f discogs-api

# Scale service
docker-compose up -d --scale discogs-api=3

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Traditional Server Deployment

### Manual Deployment

#### 1. Prepare the Server

```bash
# Install Java 24 (Amazon Corretto)
wget https://corretto.aws/downloads/latest/amazon-corretto-24-x64-linux-jdk.rpm
sudo rpm -i amazon-corretto-24-x64-linux-jdk.rpm

# Install Node.js 20
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs

# Install Yarn
npm install -g yarn

# Create application user
sudo useradd -r -s /bin/false discogs-api
sudo mkdir -p /opt/discogs-api
sudo chown discogs-api:discogs-api /opt/discogs-api
```

#### 2. Build the Application

```bash
# Clone repository
git clone https://github.com/your-org/discogs-query-api.git
cd discogs-query-api

# Build backend
mvn clean package -DskipTests

# Build frontend
cd src/main/frontend
yarn install
yarn build
cd ../../..

# Copy artifacts
sudo cp target/discogs-query-1.0-SNAPSHOT.jar /opt/discogs-api/discogs-api.jar
sudo cp -r src/main/frontend/build /opt/discogs-api/frontend/
sudo chown -R discogs-api:discogs-api /opt/discogs-api
```

#### 3. Create Systemd Service

```bash
# Create service file
sudo tee /etc/systemd/system/discogs-api.service << EOF
[Unit]
Description=Discogs Query API
After=network.target

[Service]
Type=simple
User=discogs-api
Group=discogs-api
WorkingDirectory=/opt/discogs-api
ExecStart=/usr/bin/java -jar discogs-api.jar
Environment=DISCOGS_TOKEN=your_token_here
Environment=DISCOGS_AGENT=your_app_name
Environment=SERVER_PORT=9090
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable discogs-api
sudo systemctl start discogs-api

# Check status
sudo systemctl status discogs-api
```

#### 4. Configure Reverse Proxy (Nginx)

```nginx
# /etc/nginx/sites-available/discogs-api
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Health check endpoint
        location /actuator/health {
            proxy_pass http://localhost:9090/actuator/health;
            access_log off;
        }
    }
}

# Enable site
sudo ln -s /etc/nginx/sites-available/discogs-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Cloud Platform Deployment

### AWS Deployment

#### Using AWS ECS with Fargate

```yaml
# task-definition.json
{
  "family": "discogs-api",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "1024",
  "memory": "2048",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::account:role/ecsTaskRole",
  "containerDefinitions":
    [
      {
        "name": "discogs-api",
        "image": "your-account.dkr.ecr.region.amazonaws.com/discogs-api:latest",
        "portMappings": [{ "containerPort": 9090, "protocol": "tcp" }],
        "environment": [{ "name": "SERVER_PORT", "value": "9090" }],
        "secrets":
          [
            {
              "name": "DISCOGS_TOKEN",
              "valueFrom": "arn:aws:secretsmanager:region:account:secret:discogs-token",
            },
          ],
        "healthCheck":
          {
            "command":
              [
                "CMD-SHELL",
                "wget --quiet --spider http://localhost:9090/actuator/health || exit 1",
              ],
            "interval": 30,
            "timeout": 10,
            "retries": 3,
            "startPeriod": 60,
          },
        "logConfiguration":
          {
            "logDriver": "awslogs",
            "options":
              {
                "awslogs-group": "/ecs/discogs-api",
                "awslogs-region": "us-east-1",
                "awslogs-stream-prefix": "ecs",
              },
          },
      },
    ],
}
```

#### Using AWS App Runner

```yaml
# apprunner.yaml
version: 1.0
runtime: java24
build:
  commands:
    build:
      - mvn clean package -DskipTests
      - cd src/main/frontend && yarn install && yarn build
run:
  runtime-version: 24
  command: java -jar target/discogs-query-1.0-SNAPSHOT.jar
  network:
    port: 9090
    env-port: SERVER_PORT
  env:
    - name: DISCOGS_AGENT
      value: "discogs-api-apprunner"
```

### Google Cloud Platform

#### Using Cloud Run

```yaml
# cloudbuild.yaml
steps:
  - name: "gcr.io/cloud-builders/docker"
    args: ["build", "-t", "gcr.io/$PROJECT_ID/discogs-api:$COMMIT_SHA", "."]
  - name: "gcr.io/cloud-builders/docker"
    args: ["push", "gcr.io/$PROJECT_ID/discogs-api:$COMMIT_SHA"]
  - name: "gcr.io/cloud-builders/gcloud"
    args:
      - "run"
      - "deploy"
      - "discogs-api"
      - "--image=gcr.io/$PROJECT_ID/discogs-api:$COMMIT_SHA"
      - "--region=us-central1"
      - "--platform=managed"
      - "--allow-unauthenticated"
      - "--port=9090"
      - "--memory=2Gi"
      - "--cpu=2"
      - "--set-env-vars=SERVER_PORT=9090"
      - "--set-secrets=DISCOGS_TOKEN=discogs-token:latest"
```

### Kubernetes Deployment

```yaml
# kubernetes/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: discogs-api

---
# kubernetes/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: discogs-secrets
  namespace: discogs-api
type: Opaque
stringData:
  DISCOGS_TOKEN: "your_token_here"
  DISCOGS_AGENT: "discogs-api-k8s"

---
# kubernetes/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: discogs-api
  namespace: discogs-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: discogs-api
  template:
    metadata:
      labels:
        app: discogs-api
    spec:
      containers:
        - name: discogs-api
          image: discogs-api:latest
          ports:
            - containerPort: 9090
          env:
            - name: SERVER_PORT
              value: "9090"
          envFrom:
            - secretRef:
                name: discogs-secrets
          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"
            limits:
              memory: "2Gi"
              cpu: "1000m"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 9090
            initialDelaySeconds: 60
            periodSeconds: 30
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 9090
            initialDelaySeconds: 30
            periodSeconds: 10

---
# kubernetes/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: discogs-api-service
  namespace: discogs-api
spec:
  selector:
    app: discogs-api
  ports:
    - port: 80
      targetPort: 9090
  type: ClusterIP

---
# kubernetes/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: discogs-api-ingress
  namespace: discogs-api
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
    - hosts:
        - your-domain.com
      secretName: discogs-api-tls
  rules:
    - host: your-domain.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: discogs-api-service
                port:
                  number: 80
```

## Environment Configuration

### Production Environment Variables

```bash
# Core Configuration
DISCOGS_TOKEN=your_production_token
DISCOGS_AGENT=your_production_app_name
SERVER_PORT=9090

# Security
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=secure_password

# Performance
QUERIES_TIMEOUT=59
QUERIES_FILTER_FOR_UK=false
DISCOGS_RATE_LIMIT=60

# JVM Options
JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/heapdumps

# Logging
LOGGING_LEVEL_ORG_DISCOGS_QUERY=INFO
LOGGING_LEVEL_ROOT=WARN
```

### Development vs Production Settings

| Setting      | Development | Production |
| ------------ | ----------- | ---------- |
| Log Level    | DEBUG       | INFO       |
| Memory       | 1GB         | 2-4GB      |
| Health Check | 30s         | 10s        |
| Rate Limit   | 60/min      | 60/min     |
| Cache TTL    | 10min       | 10min      |
| Timeout      | 59s         | 59s        |

## Monitoring and Health Checks

### Application Health Endpoints

- **Health Check**: `GET /actuator/health`
- **Info**: `GET /actuator/info`
- **Metrics**: `GET /actuator/metrics`
- **Mappings**: `GET /actuator/mappings`

### Docker Health Check

```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget --quiet --spider http://localhost:9090/actuator/health || exit 1
```

### Kubernetes Probes

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 9090
  initialDelaySeconds: 60
  periodSeconds: 30
  timeoutSeconds: 10
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health
    port: 9090
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

## Security Considerations

### Container Security

- Non-root user execution
- Minimal base images (Alpine)
- Security scanning with tools like Trivy
- Regular base image updates

### Network Security

- HTTPS termination at load balancer
- CORS configuration for allowed origins
- Rate limiting at application and infrastructure level
- API authentication and authorization

### Secrets Management

- Environment variables for non-sensitive config
- Secret management systems for tokens
- Avoid secrets in container images
- Regular secret rotation

## Scaling and Load Balancing

### Horizontal Scaling

```bash
# Docker Compose scaling
docker-compose up -d --scale discogs-api=3

# Kubernetes scaling
kubectl scale deployment discogs-api --replicas=5 -n discogs-api
```

### Load Balancer Configuration

```nginx
upstream discogs_api {
    server localhost:9090;
    server localhost:9091;
    server localhost:9092;
}

server {
    listen 80;
    location / {
        proxy_pass http://discogs_api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## Troubleshooting

### Common Issues

1. **Application Won't Start**
   - Check environment variables
   - Verify Java version compatibility
   - Check port availability

2. **High Memory Usage**
   - Adjust JVM heap settings
   - Monitor cache utilization
   - Check for memory leaks

3. **Rate Limiting Issues**
   - Verify Discogs API token
   - Check rate limit configuration
   - Monitor API usage

### Log Analysis

```bash
# Docker logs
docker logs discogs-query-api

# Kubernetes logs
kubectl logs -f deployment/discogs-api -n discogs-api

# Systemd logs
journalctl -u discogs-api -f
```

This deployment guide provides comprehensive instructions for deploying the Discogs Query API across various environments and platforms.
