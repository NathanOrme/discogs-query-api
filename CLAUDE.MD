This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Table of Contents

1. Project Overview
2. Claude's Understanding & Core Components
3. Priority API Endpoints & Flows
4. Development Commands
5. Architecture & Code Organization
6. Working with This Codebase
7. Common Development Tasks
8. Testing & Verification
9. Error Handling & Logging
10. Security & Performance
11. Standards Research
12. Project Knowledge & Memory Bank
13. Frontend/UI Considerations
14. Environment Configuration
15. Branch & Commit Practices

## Project Overview

This project is a backend-first full-stack application that queries the Discogs catalog. The backend is powered by Spring Boot exposing REST endpoints that proxy, enrich, and rate-limit requests to the Discogs API. A lightweight React frontend provides search and result display functionality. Docker and Swagger are used to improve developer ergonomics and visibility.

## Claude's Understanding & Core Components

### Core Components

1. **Backend (Spring Boot 3.5.4 with Java 24)**

   * **Main endpoint**: `/discogs-query/search` (POST) for batch query processing
   * **Services**: `QueryProcessingService`, `MappingService`, `ResultCalculationService` for business logic
   * **Clients**: `DiscogsAPIClientImpl` and `DiscogsWebScraperClientImpl` for external API calls  
   * **Rate limiting**: `RateLimiterServiceImpl` with configurable thresholds (60 req/min default)
   * **Caching**: Caffeine cache implementation via `CacheConfig`
   * **Documentation**: SpringDoc OpenAPI (not SpringFox) with Swagger UI
   * **Global exception handling**: `DiscogsQueryControllerAdvice` with custom exceptions
   * **Security**: Spring Security with basic auth and CORS configuration

2. **Frontend (React 19 with Material-UI and Vite)**

   * **Build tool**: Vite (not Create React App) with TypeScript support
   * **UI Framework**: Material-UI (@mui/material) with stepper-based navigation
   * **Components**: Modular architecture in `src/modules/` (QueryFields, SearchForm, Results, CheapestItem)
   * **Testing**: Jest with React Testing Library, comprehensive test coverage
   * **Styling**: Material-UI theming system with responsive design

3. **Infrastructure**

   * **Multi-stage Dockerfile**: Separate frontend (Node 24) and backend (Maven + Corretto 24) build stages
   * **Runtime**: Amazon Corretto 24 with non-root user for security
   * **Health checks**: Spring Boot Actuator integration for container monitoring
   * **Environment variables**: Comprehensive configuration via `application.yml` and Docker environment

## Priority API Endpoints & Flows

* **Primary endpoint**: `/discogs-query/search` (POST) - handles all search operations with query batching
* **Documentation**: Available at `/swagger-ui.html` and `/api-docs` endpoints  
* **Health check**: `/actuator/health` for monitoring and container health checks
* Ensure rate limiting logic is centralized and consistently applied to avoid Discogs throttling (configured via `RateLimiterServiceImpl`).
* Response shape consistency: any change to public responses must preserve backward compatibility or be versioned with clear migration notes.
* SpringDoc OpenAPI definitions should reflect reality; updates to implementation that affect request/response schemas must also update the API docs.

## Development Commands

```bash
# Backend run (development)
mvn spring-boot:run

# Run backend tests
mvn test

# Build for production
mvn package

# Frontend development server
cd src/main/frontend && yarn start
# or
cd src/main/frontend && yarn dev

# Frontend build
cd src/main/frontend && yarn build

# Frontend tests
cd src/main/frontend && yarn test

# Code formatting
cd src/main/frontend && yarn format

# Docker build (full application)
docker build -t discogs-query-api .
```

## Architecture & Code Organization

* **Controllers**: REST entry points; validate inputs and delegate to services.
* **Services**: Encapsulate business logic, external API orchestration, caching, and transformation.
* **Clients**: HTTP clients (wrapped, retry-enabled) for calling Discogs.
* **DTOs**: Request/response contracts, mapped explicitly and documented.
* **Error Handling Layer**: Custom exceptions, global handlers, and consistent error codes.
* **Configuration**: Externalized via environment variables and profiles (e.g., dev vs. prod).
* **Frontend**: Minimal React components for search; co-located or loosely coupled, but any visible change triggers UI verification.

## Working with This Codebase

* **Branching**: For every new change, create a new descriptive git branch (e.g., `feature/search-caching`, `fix/record-endpoint-error-handling`). Keep all work, including TODO resolution and test/playwright validation, on that branch until the task is complete. Do not commit directly to main/trunk; merge only after passing validation.
* **Before editing**: Run existing tests, review open TODOs near the area of change, and verify current behavior (for UI-affecting changes use Playwright MCP snapshots). Document any new concepts or deviations in the memory bank and expand this section accordingly.
* **After editing**: Run `mvn test` to catch regressions. If frontend `.tsx` files are touched, run `cd src/main/frontend && yarn test` or verify UI behavior and run Playwright MCP comparisons. Update SpringDoc annotations if schema changes, and adjust documentation.

## Common Development Tasks

* **Adding new search parameters**: Extend `DiscogsQueryController`/services, update `DiscogsAPIClientImpl`, validate inputs via Jakarta validation, adjust response DTOs, add unit tests, and update SpringDoc OpenAPI annotations.
* **Improving rate limiting**: Locate centralized rate limit logic, adjust thresholds or backoff strategies, add metrics/logging for throttled calls, and validate with integration tests.
* **Caching enhancements**: Identify cache keys, handle invalidation, and fall back gracefully if the cache layer fails. Document assumptions in memory bank.
* **Error resolution**: Search for nearby TODOs and exceptions. Attempt to resolve or annotate with context; recursively validate with tests and, if applicable, UI snapshots.

## Testing & Verification

* **Backend unit tests**: JUnit 5 with Mockito covering services and controllers. Mock external Discogs calls. Diffblue-generated tests provide additional coverage.
* **BDD tests**: Karate framework for behavior-driven development and API contract testing.
* **Integration tests**: Ensure full search flow works from `/discogs-query/search` endpoint through to Discogs API proxy with representative mock data.
* **Schema validation**: Verify SpringDoc OpenAPI definitions match actual runtime request/response shapes.
* **Frontend tests**: Jest with React Testing Library for component testing. Run with `yarn test` in frontend directory.
* **Frontend/visible changes**: Any change affecting the React UI should go through Playwright MCP before/after snapshot comparison to catch regressions.
* **Post-change validation**: Always run `mvn test` for backend and `cd src/main/frontend && yarn test` for frontend after changes. Ensure no test failures remain.

## Error Handling & Logging

* Use custom exceptions for business-level failures; map them to appropriate HTTP status codes.
* Global exception handlers must sanitize sensitive details while preserving actionable messages.
* Log with context (correlation IDs, request parameters minus secrets) to aid debugging.
* Distinguish transient failures (retryable) from permanent ones. Implement backoff and retries for Discogs calls where safe.

## Security & Performance

* **Secrets**: Never commit `DISCOGS_TOKEN` or other credentials. Use environment variables.
* **Input validation**: Sanitize and validate all incoming parameters to avoid injection or abuse.
* **Rate limiting**: Protect both outgoing Discogs usage and incoming traffic to avoid abuse.
* **Caching**: Cache safe-to-reuse responses but include time-to-live and invalidation strategy.
* **Performance**: Avoid N+1 patterns, batch when possible, and minimize payload size. Add instrumentation to identify hotspots.

## Standards Research

* When uncertain about REST conventions, error response formats, pagination strategies, or HTTP semantics, search using **DuckDuckGo** for relevant standards (e.g., RFCs, OWASP guidelines, Spring Boot idiomatic patterns).
* Record findings, chosen deviations, or adaptations in this document and the memory bank with rationale. Prefer authoritative sources (e.g., official Spring documentation, OWASP, RFCs) and note when pragmatic compromises are taken.

## Project Knowledge & Memory Bank

* Maintain named memory entries for key services, API flows, caching semantics, rate limiting strategies, and exception classifications.
* Newly discovered invariants (e.g., "Discogs returns inconsistent pagination when X occurs") should be documented here with context, where it surfaces, and any mitigations.
* Periodically reconcile the memory bank with this document to avoid drift.

## Frontend / UI Considerations

* Although backend-primary, the search UI is the user’s primary feedback surface. Visible changes to search inputs, result lists, loading states, or errors must use Playwright MCP comparisons for before/after.
* Ensure that API changes surfaced in the UI degrade gracefully: stubbed errors, empty results, and partial data should have clear user feedback.
* If the React UI begins to show signs of chronic layout/design friction or complexity, consider escalating to a discussion about a component framework (e.g., shadcn/ui) with explicit user consent before introducing a new UI library. Record any decision in the memory bank and add a check note here.

## Environment Configuration

Required environment variables for local development:

```bash
DISCOGS_TOKEN=your_discogs_api_token
DISCOGS_AGENT=your_application_name
SERVER_PORT=9090
```

Local run steps:

1. Export the required environment variables.
2. Start backend: `mvn spring-boot:run` (runs on port 9090).
3. Start frontend (if making UI changes): `cd src/main/frontend && yarn start` (Vite dev server on port 3000).
4. Visit SpringDoc Swagger UI at `http://localhost:9090/swagger-ui.html` to inspect/update API documentation.
5. Access application health check at `http://localhost:9090/actuator/health`.

## Branch & Commit Practices

* Branch naming should reflect intent, scope, and ticket (e.g., `feature/record-caching`, `fix/search-error-handling`).
* Include in commit message: what was changed, what verification was done (tests, UI diffs, SpringDoc OpenAPI updates), TODOs resolved or deliberately deferred, and any memory bank updates.
* Do not merge until all validation steps are green (mvn test, yarn test if frontend changes) and any required approvals (e.g., UI framework migration) are documented.

## Additional Documentation

### Comprehensive Project Documentation

For detailed information about the project, refer to these comprehensive documentation files:

* **[API Documentation](docs/API.md)** - Complete REST API reference with endpoints, request/response formats, authentication, and examples
* **[Frontend Architecture](docs/FRONTEND.md)** - React application structure, component organization, state management, and development patterns
* **[Testing Guide](docs/TESTING.md)** - Comprehensive testing strategy covering backend unit tests, frontend component tests, integration testing, and BDD with Karate
* **[Deployment Guide](docs/DEPLOYMENT.md)** - Docker containerization, Kubernetes deployment, cloud platform setup, and production configuration
* **[Development Setup](docs/DEVELOPMENT.md)** - Complete development environment setup, IDE configuration, debugging, and workflow guidelines
* **[Troubleshooting Guide](docs/TROUBLESHOOTING.md)** - Common issues, diagnostic commands, performance problems, and emergency recovery procedures
* **[Security Policy](SECURITY.md)** - Security architecture, vulnerability reporting, compliance standards, and secure development practices

### Quick Reference Links

- **Main README**: [README.md](README.md) - Project overview, features, and quick start guide
* **API Documentation**: Interactive Swagger UI available at `http://localhost:9090/swagger-ui.html`
* **Health Check**: Application status at `http://localhost:9090/actuator/health`
* **Security Contact**: For security issues, see [SECURITY.md](SECURITY.md) for reporting procedures
