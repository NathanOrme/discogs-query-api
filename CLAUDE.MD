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

1. **Backend (Spring Boot)**

   * REST API endpoints for searching by artist, track, format, etc.
   * Integration with the Discogs API including authenticated requests and rate limiting.
   * Global exception handling and structured error responses.
   * API documentation via Swagger/OpenAPI.
   * Caching layer (if present) to reduce redundant external calls.
2. **Frontend (React)**

   * Search UI (artist/track/format inputs) and result rendering.
   * Feedback for loading, empty, and error states.
   * Minimal dependencies to keep the surface lightweight; still subject to the UI verification flows when changes touch visible components.
3. **Infrastructure**

   * Dockerfiles for containerized development and deployment.
   * Environment variable-driven configuration (tokens, ports, modes).
   * Logging and observability (structured logs, optional metrics integration).

## Priority API Endpoints & Flows

* Identify the most-used or user-visible endpoints (e.g., `/api/search`, `/api/record/{id}`) and treat them as high priority when making changes, refactors, or adding features.
* Ensure rate limiting logic is centralized and consistently applied to avoid Discogs throttling.
* Response shape consistency: any change to public responses must preserve backward compatibility or be versioned with clear migration notes.
* Swagger/OpenAPI definitions should reflect reality; updates to implementation that affect request/response schemas must also update the API docs.

## Development Commands

```bash
# Backend run (development)
mvn spring-boot:run

# Run backend tests
mvn test

# Build for production
mvn package

# Frontend (if present)
npm start
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
* **After editing**: Run `mvn test` to catch regressions. If frontend `.tsx` files are touched, run the React test suite or verify UI behavior and run Playwright MCP comparisons. Update Swagger if schema changes, and adjust documentation.

## Common Development Tasks

* **Adding new search parameters**: Extend controller/service, update Discogs client, validate inputs, adjust response DTOs, add unit tests, and update Swagger schema.
* **Improving rate limiting**: Locate centralized rate limit logic, adjust thresholds or backoff strategies, add metrics/logging for throttled calls, and validate with integration tests.
* **Caching enhancements**: Identify cache keys, handle invalidation, and fall back gracefully if the cache layer fails. Document assumptions in memory bank.
* **Error resolution**: Search for nearby TODOs and exceptions. Attempt to resolve or annotate with context; recursively validate with tests and, if applicable, UI snapshots.

## Testing & Verification

* **Backend unit tests**: Cover services and controllers. Mock external Discogs calls.
* **Integration tests**: Ensure full search flow works (from input to Discogs API proxy to output) with representative mock or sandbox data.
* **Schema validation**: Verify that Swagger/OpenAPI definitions match actual runtime shapes.
* **Frontend/visible changes**: Any change that affects the React UI should go through the Playwright MCP before/after snapshot comparison to catch regressions.
* **Post-change validation**: Always run `mvn test` (and frontend test commands if relevant) after changes and ensure no test failures remain.

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
2. Start backend: `mvn spring-boot:run`.
3. Start frontend (if making UI changes): `npm start`.
4. Visit Swagger UI to inspect/update documentation (typically at `http://localhost:9090/swagger-ui.html`).

## Branch & Commit Practices

* Branch naming should reflect intent, scope, and ticket (e.g., `feature/record-caching`, `fix/search-error-handling`).
* Include in commit message: what was changed, what verification was done (tests, UI diffs, Swagger updates), TODOs resolved or deliberately deferred, and any memory bank updates.
* Do not merge until all validation steps are green and any required approvals (e.g., UI framework migration) are documented.
