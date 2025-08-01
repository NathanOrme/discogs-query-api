# Discogs Query API

A sophisticated full-stack application for querying the Discogs catalog with advanced search capabilities, rate limiting, and marketplace price integration. Built using Spring Boot 3.5.4 with Java 24 for the backend and React 19 with Material-UI for the frontend, this project provides a comprehensive solution for Discogs database searching with batch query processing, caching, and real-time price data.

<!-- TOC -->

- [Discogs Query](#discogs-query)
  - [Features](#features)
  - [Architecture](#architecture)
  - [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running Locally](#running-locally)
      - [Run the Backend](#run-the-backend)
      - [Run the Frontend](#run-the-frontend)
    - [Configuration](#configuration)
      - [Server Settings](#server-settings)
      - [Management Endpoints](#management-endpoints)
      - [OpenAPI Documentation](#openapi-documentation)
      - [Security](#security)
      - [Logging](#logging)
      - [Query Settings](#query-settings)
      - [Discogs API Configuration](#discogs-api-configuration)
  - [Development](#development)
    - [Code Style](#code-style)
    - [Running Tests](#running-tests)

<!-- TOC -->

## Features

### Backend Capabilities
- **Batch Query Processing**: Execute multiple searches concurrently with configurable timeouts
- **Advanced Search Types**: Support for artist, track, format, and compilation searches
- **Marketplace Integration**: Real-time price and availability data from Discogs marketplace
- **Intelligent Rate Limiting**: Token bucket algorithm with 60 requests/minute default limit
- **Multi-Level Caching**: Caffeine-based caching with 10-minute TTL and 1000 entry capacity
- **Retry Logic**: Exponential backoff retry mechanism for failed API calls
- **UK Filtering**: Optional filtering for items shipping from the UK
- **Comprehensive Error Handling**: Custom exceptions with detailed error responses

### Frontend Features
- **Material-UI Interface**: Modern, responsive UI with stepper-based navigation
- **Real-Time Search**: Instant search results with loading states and error handling
- **Results Visualization**: Organized display of search results with cheapest item highlighting
- **Export Functionality**: JSON export of search results
- **Progressive Web App**: Optimized for mobile and desktop experiences

### Infrastructure
- **SpringDoc OpenAPI**: Comprehensive API documentation with Swagger UI
- **Docker Support**: Multi-stage Dockerfile for optimized containerization
- **Health Monitoring**: Spring Boot Actuator integration with health checks
- **Security**: Spring Security with CORS support and basic authentication
- **Observability**: Structured logging with configurable levels

## Architecture

### Backend Architecture (Spring Boot 3.5.4 + Java 24)
- **Service Layer**: Interface-driven architecture with `DiscogsQueryService`, `QueryProcessingService`, and `MappingService`
- **API Integration**: `DiscogsAPIClient` and `DiscogsWebScraperClient` for external service communication
- **Rate Limiting**: Token bucket algorithm via `RateLimiterService` with configurable limits
- **Caching Strategy**: Multi-namespace Caffeine caching for API responses, marketplace data, and search results
- **Error Handling**: Comprehensive exception hierarchy with global `@ControllerAdvice` error handling
- **Retry Logic**: Exponential backoff retry service for transient failures
- **Security**: Spring Security with basic auth and CORS configuration

### Frontend Architecture (React 19 + Vite + Material-UI)
- **Build System**: Vite for fast development and optimized production builds
- **Component Architecture**: Modular components in `/modules` with dedicated testing
- **State Management**: React hooks for local state with context for shared data
- **UI Framework**: Material-UI (@mui/material) with consistent theming
- **Testing**: Jest with React Testing Library for comprehensive component testing
- **Development**: TypeScript for type safety and better developer experience

### Infrastructure
- **Containerization**: Multi-stage Docker build with Node 24 (frontend) and Amazon Corretto 24 (backend)
- **Documentation**: SpringDoc OpenAPI with Swagger UI at `/swagger-ui.html`
- **Monitoring**: Spring Boot Actuator with health checks and metrics endpoints
- **Configuration**: Environment-driven configuration via `application.yml` and Docker environment variables

## Getting Started

### Prerequisites

- **Java 24 or higher** (Amazon Corretto 24 recommended)
- **Maven 3.6.0 or higher**
- **Node.js 20 or higher** with Yarn
- **Docker** (optional, for containerized deployment)
- **Discogs API Token** (obtain from [Discogs Developer Portal](https://www.discogs.com/developers/))

### Environment Variables

Before running the application, set these required environment variables:

```bash
export DISCOGS_TOKEN=your_discogs_api_token
export DISCOGS_AGENT=your_application_name
export SERVER_PORT=9090
```

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/discogs-query-api.git
   cd discogs-query-api
   ```

2. **Install Backend Dependencies**
   ```bash
   mvn clean install
   ```

3. **Install Frontend Dependencies**
   ```bash
   cd src/main/frontend
   yarn install
   cd ../../..
   ```

### Running Locally

#### Option 1: Separate Backend and Frontend

1. **Start the Backend** (Terminal 1)
   ```bash
   mvn spring-boot:run
   ```
   Backend will be available at `http://localhost:9090`

2. **Start the Frontend** (Terminal 2)
   ```bash
   cd src/main/frontend
   yarn start
   ```
   Frontend will be available at `http://localhost:3000`

#### Option 2: Docker (Recommended for Production)

```bash
docker build -t discogs-query-api .
docker run -p 9090:9090 \
  -e DISCOGS_TOKEN=your_token \
  -e DISCOGS_AGENT=your_agent \
  discogs-query-api
```

Application will be available at `http://localhost:9090`

### Quick Start Verification

1. **Check Backend Health**: `http://localhost:9090/actuator/health`
2. **View API Documentation**: `http://localhost:9090/swagger-ui.html`
3. **Access Frontend**: `http://localhost:3000` (if running separately)

### Configuration

#### Server Settings

- **Port:**  
  The application is set to run on port `9090`.

```yaml
server:
port: 9090
```

#### Management Endpoints

- **Management Endpoints:**  
  Actuator endpoint mappings are exposed for management via `/mappings`.

```yaml
management:
endpoints:
web:
exposure:
include: mappings
```

#### OpenAPI Documentation

- **API Docs Path:**  
  OpenAPI documentation is available at `/api-docs`.

- **Show Actuator:**  
  Actuator endpoints are visible in the API documentation.

```yaml
springdoc:
api-docs:
path: /api-docs
show-actuator: true
```

#### Security

- **Default User Credentials:**
  - Username: `username`
  - Password: `password`

```yaml
spring:
security:
user:
name: username
password: password
```

#### Logging

- **Log Output:**  
  Colored output is enabled in the console.

- **Log Levels:**
  - `INFO` for web requests.
  - `WARN` for Hibernate.
  - `DEBUG` for custom Discogs query logging.

```yaml
spring:
output:
ansi:
enabled: always # Enable colored output in the console

logging:
level:
root: INFO
org.springframework.web: INFO # Enable INFO logging for web requests
org.hibernate: WARN # Reduce verbosity of Hibernate logs
org.discogs.query: DEBUG
```

#### Query Settings

- **Timeout**: Queries timeout after 59 seconds (configurable)
- **UK Filter**: UK filtering for queries is disabled by default
- **Search Collection**: Collection searching is enabled by default

```yaml
queries:
  timeout: 59
  filterForUk: false
  searchCollection: true
```

#### Discogs API Configuration

- **Base URL:**  
  Discogs API URL: `https://api.discogs.com/`

- **Endpoints:**

  - Search: `database/search`
  - Release: `releases/`
  - Marketplace Check: `marketplace/stats/`

- **Agent & Token:**  
  These are configured via environment variables:

  - Agent: `${DISCOGS_AGENT}`
  - Token: `${DISCOGS_TOKEN}`

- **Page Size:**  
  Pagination is set to 20 items per page.

- **Rate Limit:**  
  The rate limit is set to 60 requests per minute.

```yaml
discogs:
url: https://api.discogs.com/
baseUrl: https://www.discogs.com
search: database/search
release: releases/
marketplaceCheck: marketplace/stats/
agent: ${DISCOGS_AGENT}
page-size: 20
rate-limit: 60
token: ${DISCOGS_TOKEN}
```

## Development

### Code Style

- Java: Follow standard Java coding conventions and use Javadoc for documentation.
- JavaScript: Adhere to standard JavaScript/React best practices.

### Running Tests

#### Backend Tests
```bash
# Run all backend tests (JUnit 5 + Mockito + Karate BDD)
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run only unit tests
mvn test -Dtest="**/*Test"

# Run only integration tests
mvn test -Dtest="**/*IT"
```

#### Frontend Tests
```bash
cd src/main/frontend

# Run all frontend tests (Jest + React Testing Library)
yarn test

# Run tests in watch mode
yarn test --watch

# Run tests with coverage
yarn test --coverage

# Format code
yarn format
```

#### Test Coverage
- **Backend**: JUnit 5 with Mockito for unit tests, Karate for BDD/API testing, Diffblue for additional coverage
- **Frontend**: Jest with React Testing Library for component testing
- **Integration**: Full API flow testing with mock Discogs responses

## API Endpoints

### Primary Endpoint
- **POST** `/discogs-query/search` - Execute batch queries with marketplace integration

### Documentation & Monitoring
- **GET** `/swagger-ui.html` - Interactive API documentation
- **GET** `/api-docs` - OpenAPI specification
- **GET** `/actuator/health` - Application health check
- **GET** `/actuator/mappings` - Request mapping information

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Check the [CLAUDE.md](CLAUDE.md) for development guidelines
- Review the [SECURITY.md](SECURITY.md) for security policies
- Open an issue on GitHub for bug reports or feature requests
