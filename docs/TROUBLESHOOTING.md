# Troubleshooting Guide

## Overview

This guide provides solutions to common issues encountered when developing, deploying, and running the Discogs Query API. It covers backend services, frontend applications, Docker containers, and integration problems.

## Quick Diagnostic Commands

### Health Check Commands
```bash
# Application health
curl http://localhost:9090/actuator/health

# Backend API test
curl -X POST "http://localhost:9090/discogs-query/search" \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{"queries":[{"artist":"Test"}]}'

# Frontend availability
curl http://localhost:3000

# Docker container status
docker ps
docker logs discogs-query-api
```

### System Information
```bash
# Java version
java -version

# Maven version
mvn -version

# Node.js and Yarn versions
node --version
yarn --version

# Memory usage
free -h  # Linux
docker stats  # Docker containers
```

## Application Startup Issues

### Backend Won't Start

#### Problem: Java Version Mismatch
```
Error: A JNI error has occurred, please check your installation and try again
Exception in thread "main" java.lang.UnsupportedClassVersionError
```

**Solution:**
```bash
# Check Java version
java -version

# Should show Java 24. If not, install correct version:
# Using SDKMAN
sdk install java 24.0.0-amzn

# Set JAVA_HOME
export JAVA_HOME=/path/to/java24
export PATH=$JAVA_HOME/bin:$PATH
```

#### Problem: Port Already in Use
```
Web server failed to start. Port 9090 was already in use.
```

**Solution:**
```bash
# Find process using port 9090
lsof -i :9090
# or on Windows
netstat -ano | findstr :9090

# Kill the process
kill -9 <PID>
# or on Windows
taskkill /PID <PID> /F

# Alternative: Change port
export SERVER_PORT=9091
mvn spring-boot:run
```

#### Problem: Missing Environment Variables
```
2024-01-01 12:00:00.000 ERROR --- [main] o.s.boot.SpringApplication: Application run failed
```

**Solution:**
```bash
# Check environment variables
echo $DISCOGS_TOKEN
echo $DISCOGS_AGENT

# Set missing variables
export DISCOGS_TOKEN=your_token_here
export DISCOGS_AGENT=your_app_name

# Or create .env file
cat > .env << EOF
DISCOGS_TOKEN=your_token_here
DISCOGS_AGENT=your_app_name
EOF

source .env
```

#### Problem: Maven Build Failure
```
[ERROR] Failed to execute goal on project discogs-query: Could not resolve dependencies
```

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Force update dependencies
mvn clean install -U

# Check Maven settings
mvn help:effective-settings

# Verify network connectivity
ping repo1.maven.org
```

### Frontend Won't Start

#### Problem: Node.js Version Incompatibility
```
error engine-strict@undefined: The engine "node" is incompatible with this module
```

**Solution:**
```bash
# Check Node.js version
node --version

# Should be 20.x or higher. Install correct version:
# Using nvm
nvm install 20
nvm use 20

# Using package manager
# macOS: brew install node@20
# Windows: choco install nodejs --version=20.0.0
```

#### Problem: Yarn Installation Issues
```
error An unexpected error occurred: "EACCES: permission denied"
```

**Solution:**
```bash
# Fix npm permissions
mkdir ~/.npm-global
npm config set prefix '~/.npm-global'
export PATH=~/.npm-global/bin:$PATH

# Reinstall Yarn
npm install -g yarn

# Or use npx
npx yarn install
```

#### Problem: Frontend Build Failures
```
Module not found: Error: Can't resolve '@mui/material'
```

**Solution:**
```bash
cd src/main/frontend

# Clear node_modules
rm -rf node_modules package-lock.json yarn.lock

# Reinstall dependencies
yarn install

# Clear Vite cache
rm -rf node_modules/.vite

# Rebuild
yarn build
```

## Runtime Issues

### API Integration Problems

#### Problem: Discogs API Rate Limiting
```
HTTP 429 Too Many Requests
```

**Solution:**
```bash
# Check rate limiter configuration
curl http://localhost:9090/actuator/configprops | grep rate

# Adjust rate limit in application.yml
discogs:
  rate-limit: 30  # Reduce from default 60

# Check current rate limit status
curl http://localhost:9090/actuator/metrics/rate.limiter.requests
```

#### Problem: Invalid Discogs Token
```
HTTP 401 Unauthorized - Invalid consumer.
```

**Solution:**
```bash
# Verify token format
echo $DISCOGS_TOKEN | wc -c  # Should be 40+ characters

# Test token directly with Discogs API
curl -H "Authorization: Discogs token=$DISCOGS_TOKEN" \
     https://api.discogs.com/database/search?q=test

# Generate new token at https://www.discogs.com/settings/developers
```

#### Problem: Network Connectivity Issues
```
java.net.ConnectException: Connection refused
```

**Solution:**
```bash
# Test external connectivity
curl -I https://api.discogs.com/

# Check DNS resolution
nslookup api.discogs.com

# Test from application host
docker exec -it discogs-query-api curl -I https://api.discogs.com/

# Check firewall/proxy settings
export HTTP_PROXY=http://proxy:8080
export HTTPS_PROXY=http://proxy:8080
```

### Performance Issues

#### Problem: High Memory Usage
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution:**
```bash
# Increase heap size
export JAVA_OPTS="-Xmx4g -Xms2g"

# Enable heap dump on OOM
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/"

# Monitor memory usage
jconsole  # Connect to local process
# or
curl http://localhost:9090/actuator/metrics/jvm.memory.used

# Analyze heap dumps
jhat heapdump.hprof
# or use Eclipse MAT
```

#### Problem: Slow Response Times
```
Request timeout after 59 seconds
```

**Solution:**
```bash
# Check cache hit rates
curl http://localhost:9090/actuator/metrics/cache.gets

# Monitor database connections
curl http://localhost:9090/actuator/metrics/hikaricp.connections

# Analyze thread pools
curl http://localhost:9090/actuator/metrics/executor.active

# Increase timeout if needed
# application.yml
queries:
  timeout: 120  # Increase from 59 seconds
```

#### Problem: Cache Issues
```
Cache miss rate is too high
```

**Solution:**
```bash
# Check cache configuration
curl http://localhost:9090/actuator/caches

# Monitor cache statistics
curl http://localhost:9090/actuator/metrics/cache.size
curl http://localhost:9090/actuator/metrics/cache.evictions

# Adjust cache settings in application.yml
spring:
  cache:
    caffeine:
      spec: maximumSize=2000,expireAfterWrite=20m  # Increase size and TTL
```

## Docker Issues

### Container Won't Start

#### Problem: Docker Build Failures
```
ERROR [backend-builder 4/4] RUN mvn clean package -DskipTests
```

**Solution:**
```bash
# Check Docker daemon
docker version

# Build with verbose output
docker build --progress=plain -t discogs-query-api .

# Build individual stages
docker build --target backend-builder -t backend-test .

# Check available memory
docker system df
docker system prune  # Clean up space
```

#### Problem: Container Exits Immediately
```
Container discogs-query-api exited with code 1
```

**Solution:**
```bash
# Check container logs
docker logs discogs-query-api

# Run in interactive mode
docker run -it --entrypoint /bin/bash discogs-query-api

# Check environment variables
docker exec discogs-query-api env

# Verify file permissions
docker exec discogs-query-api ls -la /app/
```

#### Problem: Health Check Failures
```
Health check failed
```

**Solution:**
```bash
# Test health check manually
docker exec discogs-query-api wget --quiet --spider http://localhost:9090/actuator/health

# Check application logs
docker logs -f discogs-query-api

# Disable health check temporarily
docker run --health-cmd='' discogs-query-api
```

### Network Issues

#### Problem: Container Can't Connect to External APIs
```
UnknownHostException: api.discogs.com
```

**Solution:**
```bash
# Test DNS resolution
docker exec discogs-query-api nslookup api.discogs.com

# Check network configuration
docker network ls
docker network inspect bridge

# Use host networking temporarily
docker run --network host discogs-query-api

# Configure custom DNS
docker run --dns 8.8.8.8 discogs-query-api
```

## Database Issues

### H2 Database Problems

#### Problem: Database Connection Failures
```
Connection is not available, request timed out after 30000ms
```

**Solution:**
```bash
# Check H2 console (development)
open http://localhost:9090/h2-console

# Verify datasource configuration
curl http://localhost:9090/actuator/configprops | grep datasource

# Reset database (development only)
rm -rf ~/discogs-query-db*
```

#### Problem: Database Lock Issues
```
Database may be already in use: "Locked by another process"
```

**Solution:**
```bash
# Find and kill processes holding database locks
lsof | grep discogs-query-db
kill -9 <PID>

# Use different database file
export SPRING_DATASOURCE_URL=jdbc:h2:file:./data/discogs-db-new

# Switch to in-memory database for development
export SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
```

## Security Issues

### Authentication Problems

#### Problem: Basic Auth Not Working
```
HTTP 401 Unauthorized
```

**Solution:**
```bash
# Check credentials
curl -u username:password http://localhost:9090/actuator/health

# Verify security configuration
grep -r "spring.security" src/main/resources/

# Test with different client
curl -H "Authorization: Basic $(echo -n username:password | base64)" \
     http://localhost:9090/actuator/health
```

#### Problem: CORS Issues
```
CORS policy: No 'Access-Control-Allow-Origin' header
```

**Solution:**
```bash
# Check CORS configuration
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     http://localhost:9090/discogs-query/search

# Update allowed origins
export ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com

# Or configure in application.yml
spring:
  security:
    allowed-origins: ${ALLOWED_ORIGINS:*}
```

## Testing Issues

### Backend Test Failures

#### Problem: Tests Failing Due to External Dependencies
```
DiscogsAPIClientImplTest > testGetSearchResults() FAILED
```

**Solution:**
```bash
# Run tests with proper mocking
mvn test -Dtest="DiscogsAPIClientImplTest" -Dspring.profiles.active=test

# Check test configuration
cat src/test/resources/application-test.yml

# Use WireMock for external API testing
# Add to test dependencies:
# <dependency>
#   <groupId>com.github.tomakehurst</groupId>
#   <artifactId>wiremock-jre8</artifactId>
#   <scope>test</scope>
# </dependency>
```

#### Problem: Integration Tests Timing Out
```
Test timed out after 30 seconds
```

**Solution:**
```bash
# Increase test timeout
mvn test -Dtest.timeout=60000

# Or in test class:
@Test(timeout = 60000)
public void testLongRunningOperation() {
    // test code
}

# Use @Timeout annotation (JUnit 5)
@Test
@Timeout(value = 60, unit = TimeUnit.SECONDS)
```

### Frontend Test Issues

#### Problem: React Testing Library Issues
```
Unable to find an element with the text: "Expected Text"
```

**Solution:**
```bash
cd src/main/frontend

# Debug with screen.debug()
# Add to test:
# screen.debug();

# Use findBy* queries for async elements
# await screen.findByText("Expected Text");

# Check DOM structure
# screen.getByTestId("component-id");

# Run specific test with verbose output
yarn test --verbose App.test.tsx
```

## Monitoring and Logging

### Log Analysis

#### Problem: Missing Log Output
```
Application running but no logs visible
```

**Solution:**
```bash
# Check logging configuration
curl http://localhost:9090/actuator/loggers

# Enable debug logging for specific package
curl -X POST http://localhost:9090/actuator/loggers/org.discogs.query \
     -H "Content-Type: application/json" \
     -d '{"configuredLevel":"DEBUG"}'

# Check log files (if file logging enabled)
tail -f logs/application.log

# Docker logs
docker logs -f discogs-query-api
```

#### Problem: Too Much Log Output
```
Logs are flooding with DEBUG messages
```

**Solution:**
```bash
# Reduce log levels
curl -X POST http://localhost:9090/actuator/loggers/org.springframework \
     -H "Content-Type: application/json" \
     -d '{"configuredLevel":"WARN"}'

# Configure in application.yml
logging:
  level:
    org.discogs.query: INFO
    org.springframework: WARN
    org.hibernate: ERROR
```

### Metrics Issues

#### Problem: Metrics Not Available
```
404 Not Found on /actuator/metrics
```

**Solution:**
```bash
# Check actuator configuration
curl http://localhost:9090/actuator

# Enable metrics endpoint
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

# Verify micrometer dependency in pom.xml
grep -A5 -B5 micrometer pom.xml
```

## IDE-Specific Issues

### IntelliJ IDEA

#### Problem: Hot Reload Not Working
```
Changes not reflected without restart
```

**Solution:**
1. Enable automatic compilation:
   - Settings → Build → Compiler → Build project automatically
2. Enable Run Dashboard:
   - Run → Edit Configurations → Enable "On Update action: Hot swap classes and update trigger file if failed"
3. Install Spring Boot plugin
4. Use Spring Boot DevTools dependency

#### Problem: Maven Import Issues
```
Cannot resolve org.springframework.boot:spring-boot-starter-web
```

**Solution:**
1. Invalidate Caches and Restart
2. Reimport Maven project
3. Check Maven settings: Settings → Build Tools → Maven
4. Verify network connectivity to Maven Central

### VS Code

#### Problem: Java Extension Issues
```
Java projects won't load properly
```

**Solution:**
```bash
# Reload VS Code window
# Ctrl+Shift+P → "Developer: Reload Window"

# Check Java extension pack
code --list-extensions | grep java

# Clear workspace cache
rm -rf .vscode/

# Verify Java installation path in settings.json
{
    "java.home": "/path/to/java24"
}
```

## Emergency Recovery Procedures

### Complete Environment Reset

#### Backend Reset
```bash
# Stop all Java processes
pkill -f "spring-boot"

# Clear Maven cache
rm -rf ~/.m2/repository

# Reset environment
unset $(env | grep DISCOGS | cut -d= -f1)
source .env

# Clean build
mvn clean install -U
```

#### Frontend Reset
```bash
cd src/main/frontend

# Stop all Node processes
pkill -f node

# Complete cleanup
rm -rf node_modules package-lock.json yarn.lock .next .vite

# Reinstall everything
yarn install
yarn build
```

#### Docker Reset
```bash
# Stop all containers
docker stop $(docker ps -aq)

# Remove containers and images
docker system prune -a

# Rebuild from scratch
docker build --no-cache -t discogs-query-api .
```

### Data Recovery

#### Application State Recovery
```bash
# Export current data (if applicable)
curl http://localhost:9090/actuator/configprops > config-backup.json

# Backup logs
cp logs/application.log logs/application-backup-$(date +%Y%m%d).log

# Database backup (if using file-based H2)
cp data/discogs-db.mv.db data/discogs-db-backup-$(date +%Y%m%d).mv.db
```

## Getting Help

### Community Resources
- **GitHub Issues**: Report bugs and request features
- **Documentation**: Check docs/ directory for detailed guides
- **Stack Overflow**: Tag questions with `discogs-api`, `spring-boot`, `react`
- **Discord/Slack**: Join project communication channels

### Escalation Process
1. **Self-service**: Use this troubleshooting guide
2. **Documentation**: Check project documentation in docs/
3. **Search**: Look for existing GitHub issues
4. **Report**: Create new issue with reproduction steps
5. **Support**: Contact maintainers for critical issues

### Information to Include in Bug Reports
- Operating System and version
- Java version (`java -version`)
- Node.js version (`node --version`)
- Maven version (`mvn -version`)
- Docker version (`docker --version`)
- Complete error logs
- Steps to reproduce
- Expected vs actual behavior
- Configuration files (sanitized)

This troubleshooting guide should help resolve most common issues encountered with the Discogs Query API. For issues not covered here, please refer to the project documentation or create a GitHub issue with detailed information.