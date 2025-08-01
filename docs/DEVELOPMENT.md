# Development Setup Guide

## Overview

This guide provides comprehensive instructions for setting up a development environment for the Discogs Query API. It covers all necessary tools, configurations, and best practices for contributing to the project.

## System Requirements

### Hardware Requirements
- **CPU**: Multi-core processor (4+ cores recommended)
- **Memory**: 8GB RAM minimum (16GB recommended)
- **Storage**: 10GB free disk space
- **Network**: Stable internet connection for API calls and dependency downloads

### Operating System Support
- **Windows 10/11** with WSL2 (recommended) or native
- **macOS** 11.0 or later
- **Linux** (Ubuntu 20.04+, CentOS 8+, or equivalent)

## Prerequisites Installation

### 1. Java Development Kit (JDK)

#### Amazon Corretto 24 (Recommended)
```bash
# Windows (using Chocolatey)
choco install corretto24jdk

# macOS (using Homebrew)
brew install --cask corretto24

# Linux (Ubuntu/Debian)
wget https://corretto.aws/downloads/latest/amazon-corretto-24-x64-linux-jdk.deb
sudo dpkg -i amazon-corretto-24-x64-linux-jdk.deb

# Linux (CentOS/RHEL)
wget https://corretto.aws/downloads/latest/amazon-corretto-24-x64-linux-jdk.rpm
sudo rpm -i amazon-corretto-24-x64-linux-jdk.rpm
```

#### Verify Java Installation
```bash
java -version
# Expected output: openjdk version "24" 2024-09-17
javac -version
# Expected output: javac 24
```

### 2. Apache Maven

#### Installation
```bash
# Windows (using Chocolatey)
choco install maven

# macOS (using Homebrew)
brew install maven

# Linux (Ubuntu/Debian)
sudo apt update
sudo apt install maven

# Manual installation (all platforms)
wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
tar -xzf apache-maven-3.9.6-bin.tar.gz
sudo mv apache-maven-3.9.6 /opt/maven
export PATH=/opt/maven/bin:$PATH
```

#### Verify Maven Installation
```bash
mvn -version
# Expected output: Apache Maven 3.9.6 or higher
```

### 3. Node.js and Yarn

#### Node.js Installation
```bash
# Windows (using Chocolatey)
choco install nodejs

# macOS (using Homebrew)
brew install node

# Linux (using NodeSource repository)
curl -fsSL https://rpm.nodesource.com/setup_20.x | sudo bash -
sudo yum install -y nodejs

# Alternative: Using Node Version Manager (nvm)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 20
nvm use 20
```

#### Yarn Installation
```bash
# Install Yarn globally
npm install -g yarn

# Verify installation
node --version  # Should be 20.x or higher
yarn --version  # Should be 1.22.x or higher
```

### 4. Git

#### Installation
```bash
# Windows (using Chocolatey)
choco install git

# macOS (using Homebrew)
brew install git

# Linux (Ubuntu/Debian)
sudo apt install git

# Linux (CentOS/RHEL)
sudo yum install git
```

#### Git Configuration
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
git config --global init.defaultBranch main
```

### 5. Docker (Optional but Recommended)

#### Installation
```bash
# Windows: Download Docker Desktop from docker.com
# macOS: Download Docker Desktop from docker.com

# Linux (Ubuntu)
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
```

### 6. IDE Setup

#### IntelliJ IDEA (Recommended)
1. Download from [JetBrains website](https://www.jetbrains.com/idea/)
2. Install required plugins:
   - Spring Boot
   - Lombok
   - JavaScript and TypeScript
   - React support

#### VS Code Alternative
```bash
# Install VS Code
# Windows: choco install vscode
# macOS: brew install --cask visual-studio-code
# Linux: snap install code --classic

# Install extensions
code --install-extension vscjava.vscode-java-pack
code --install-extension vscjava.vscode-spring-boot-dashboard
code --install-extension ms-vscode.vscode-typescript-next
code --install-extension bradlc.vscode-tailwindcss
```

## Project Setup

### 1. Clone Repository

```bash
# Clone the repository
git clone https://github.com/your-org/discogs-query-api.git
cd discogs-query-api

# Verify project structure
ls -la
# Should show: pom.xml, src/, Dockerfile, README.md, etc.
```

### 2. Environment Configuration

#### Create Environment File
```bash
# Create .env file in project root
cat > .env << EOF
# Required Discogs API Configuration
DISCOGS_TOKEN=your_discogs_api_token_here
DISCOGS_AGENT=discogs-query-api-dev

# Optional Configuration
SERVER_PORT=9090
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:9090
QUERIES_TIMEOUT=59
QUERIES_FILTER_FOR_UK=false
QUERIES_SEARCH_COLLECTION=true

# Development Settings
LOGGING_LEVEL_ORG_DISCOGS_QUERY=DEBUG
LOGGING_LEVEL_ROOT=INFO
SPRING_PROFILES_ACTIVE=dev
EOF
```

#### Load Environment Variables
```bash
# Linux/macOS
source .env
export $(cut -d= -f1 .env)

# Windows (PowerShell)
Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=')
    Set-Content env:\$name $value
}
```

### 3. Backend Setup

#### Install Dependencies
```bash
# Install Maven dependencies
mvn clean install

# Verify successful build
mvn compile
```

#### IDE Configuration

##### IntelliJ IDEA
1. Open project in IntelliJ IDEA
2. Configure Project SDK:
   - File → Project Structure → Project
   - Set Project SDK to Java 24
   - Set Project language level to 24
3. Configure Maven:
   - File → Settings → Build Tools → Maven
   - Verify Maven home directory
   - Enable "Import Maven projects automatically"
4. Configure Lombok:
   - Install Lombok plugin
   - Enable annotation processing:
     - Settings → Build → Compiler → Annotation Processors
     - Check "Enable annotation processing"

##### VS Code
```bash
# Open project in VS Code
code .

# Configure Java runtime in .vscode/settings.json
mkdir -p .vscode
cat > .vscode/settings.json << EOF
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-24",
            "path": "/path/to/java24",
            "default": true
        }
    ],
    "java.compile.nullAnalysis.mode": "automatic",
    "spring-boot.ls.java.home": "/path/to/java24"
}
EOF
```

#### Running Backend
```bash
# Method 1: Maven Spring Boot plugin
mvn spring-boot:run

# Method 2: Java command
mvn package -DskipTests
java -jar target/discogs-query-1.0-SNAPSHOT.jar

# Method 3: IDE run configuration
# Create run configuration in IDE with main class: org.discogs.query.DiscogsApplication
```

#### Verify Backend Setup
```bash
# Check application health
curl http://localhost:9090/actuator/health

# Expected response:
# {"status":"UP"}

# Check Swagger UI
open http://localhost:9090/swagger-ui.html
```

### 4. Frontend Setup

#### Navigate to Frontend Directory
```bash
cd src/main/frontend
```

#### Install Dependencies
```bash
# Install Node.js dependencies
yarn install

# Verify installation
yarn list --depth=0
```

#### IDE Configuration for Frontend

##### VS Code Extensions
```bash
# Install React/TypeScript extensions
code --install-extension ms-vscode.typescript-hero
code --install-extension bradlc.vscode-tailwindcss
code --install-extension ms-vscode.vscode-json
code --install-extension esbenp.prettier-vscode
```

##### Configure Prettier
```bash
# Create .prettierrc
cat > .prettierrc << EOF
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false
}
EOF
```

#### Running Frontend
```bash
# Start development server
yarn start
# or
yarn dev

# The frontend will be available at http://localhost:3000
```

#### Verify Frontend Setup
1. Open http://localhost:3000 in browser
2. Verify Material-UI components load correctly
3. Test navigation between stepper steps
4. Ensure backend API integration works

### 5. Development Workflow

#### Running Full Application
```bash
# Terminal 1: Start backend
mvn spring-boot:run

# Terminal 2: Start frontend
cd src/main/frontend
yarn start

# Access application at http://localhost:3000
```

#### Docker Development Setup
```bash
# Build Docker image
docker build -t discogs-query-api:dev .

# Run with development environment
docker run -d \
  --name discogs-dev \
  -p 9090:9090 \
  -e DISCOGS_TOKEN=your_token \
  -e DISCOGS_AGENT=dev-environment \
  discogs-query-api:dev
```

## Development Tools and Configuration

### 1. Code Quality Tools

#### Maven Configuration
```xml
<!-- Already configured in pom.xml -->
<!-- Checkstyle, PMD, SpotBugs plugins -->
```

#### Run Code Quality Checks
```bash
# Run all checks
mvn verify

# Individual checks
mvn checkstyle:check
mvn pmd:check
mvn spotbugs:check
```

#### Frontend Code Quality
```bash
cd src/main/frontend

# Format code
yarn format

# Check formatting
yarn format:check

# Run linting (if configured)
yarn lint
```

### 2. Testing Setup

#### Backend Testing
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test
mvn test -Dtest="DiscogsQueryServiceImplTest"

# Run integration tests only
mvn test -Dtest="**/*IT"
```

#### Frontend Testing
```bash
cd src/main/frontend

# Run all tests
yarn test

# Run tests in watch mode
yarn test --watch

# Run tests with coverage
yarn test --coverage

# Run specific test file
yarn test App.test.tsx
```

### 3. Debugging Configuration

#### Backend Debugging

##### IntelliJ IDEA
1. Create Debug Configuration:
   - Run → Edit Configurations
   - Add Application configuration
   - Main class: `org.discogs.query.DiscogsApplication`
   - VM options: `-Dspring.profiles.active=dev`
   - Environment variables: Load from .env file

##### VS Code
```json
// .vscode/launch.json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug DiscogsApplication",
            "request": "launch",
            "mainClass": "org.discogs.query.DiscogsApplication",
            "projectName": "discogs-query",
            "env": {
                "DISCOGS_TOKEN": "your_token_here",
                "DISCOGS_AGENT": "debug-session"
            }
        }
    ]
}
```

#### Frontend Debugging
- Chrome DevTools for React components
- VS Code debugger with Chrome extension
- React Developer Tools browser extension

### 4. Database and External Services

#### H2 Database (Development)
```yaml
# application-dev.yml (if using profiles)
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
```

#### Mock External Services
```java
// For development, consider mocking Discogs API
@Component
@Profile("dev")
public class MockDiscogsAPIClient implements DiscogsAPIClient {
    // Implementation for development
}
```

## Common Development Tasks

### 1. Adding New Features

#### Backend Feature Development
1. Create feature branch: `git checkout -b feature/new-feature`
2. Add service interface and implementation
3. Create controller endpoint
4. Add DTOs and validation
5. Write unit and integration tests
6. Update OpenAPI documentation

#### Frontend Feature Development
1. Create new component in appropriate module
2. Add TypeScript interfaces
3. Implement component with Material-UI
4. Add component tests
5. Update routing or navigation
6. Test integration with backend API

### 2. Working with APIs

#### Testing API Endpoints
```bash
# Using curl
curl -X POST "http://localhost:9090/discogs-query/search" \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "queries": [
      {
        "artist": "The Beatles",
        "album": "Abbey Road"
      }
    ]
  }'

# Using Postman collection (create and share)
# Using Swagger UI at http://localhost:9090/swagger-ui.html
```

### 3. Performance Testing

#### Backend Performance
```bash
# JProfiler, VisualVM, or other profiling tools
# Memory analysis with heap dumps
java -XX:+HeapDumpOnOutOfMemoryError -jar target/discogs-query-1.0-SNAPSHOT.jar

# Load testing with Apache Bench
ab -n 1000 -c 10 -H "Content-Type: application/json" \
   -p test-data.json \
   http://localhost:9090/discogs-query/search
```

#### Frontend Performance
```bash
# Bundle analysis
cd src/main/frontend
yarn build
npx vite-bundle-analyzer build/static/js/*.js
```

## Troubleshooting Common Issues

### 1. Build Issues

#### Maven Build Failures
```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Force update dependencies
mvn clean install -U

# Skip tests temporarily
mvn clean install -DskipTests
```

#### Frontend Build Failures
```bash
cd src/main/frontend

# Clear node_modules and reinstall
rm -rf node_modules yarn.lock
yarn install

# Clear Vite cache
rm -rf node_modules/.vite
```

### 2. Runtime Issues

#### Port Already in Use
```bash
# Find process using port 9090
lsof -i :9090
# or
netstat -tulpn | grep 9090

# Kill process
kill -9 <PID>
```

#### Environment Variables Not Loading
```bash
# Verify environment variables
printenv | grep DISCOGS

# Check Spring Boot configuration
curl http://localhost:9090/actuator/env
```

### 3. IDE Issues

#### IntelliJ IDEA
- Invalidate Caches and Restart
- Reimport Maven project
- Check Project Structure settings

#### VS Code
- Reload window: Ctrl+Shift+P → "Developer: Reload Window"
- Check Java extension pack
- Verify workspace settings

## Git Workflow

### Branch Strategy
```bash
# Feature development
git checkout -b feature/feature-name
git commit -m "feat: add new feature"
git push origin feature/feature-name

# Bug fixes
git checkout -b fix/bug-description
git commit -m "fix: resolve bug description"
git push origin fix/bug-description
```

### Commit Message Convention
```
type(scope): description

Types: feat, fix, docs, style, refactor, test, chore
Scope: backend, frontend, api, config, etc.

Examples:
feat(api): add marketplace price integration
fix(frontend): resolve stepper navigation issue
docs(readme): update installation instructions
test(backend): add rate limiter unit tests
```

## Performance and Monitoring

### Local Monitoring
```bash
# JVM monitoring
jconsole

# Application metrics
curl http://localhost:9090/actuator/metrics

# Health check
curl http://localhost:9090/actuator/health
```

### Memory Management
```bash
# JVM options for development
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"

# Heap dump analysis
jmap -dump:format=b,file=heapdump.hprof <PID>
```

This comprehensive development setup guide ensures all developers can quickly and consistently set up their development environment for the Discogs Query API project.