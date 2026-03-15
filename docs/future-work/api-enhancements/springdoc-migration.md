# SpringDoc OpenAPI Migration (Remove SpringFox)

**Priority:** P1 — High
**Category:** API Enhancements
**Estimated Effort:** 0.5–1 day
**Status:** URGENT — SpringFox last released in 2020, incompatible with Spring Boot 3+

---

## Problem

`SpringFoxConfig.java` configures the deprecated SpringFox library. SpringFox is:
- Abandoned (last release 2021)
- Incompatible with Spring Boot 3.x+ Spring Security and actuator auto-configuration
- Likely causing startup warnings or conflicts

The CLAUDE.MD already says "Documentation: SpringDoc OpenAPI (not SpringFox)" but
the code still has `SpringFoxConfig.java` — this is a contradiction that needs fixing.

---

## Steps

### 1. Remove SpringFox

Remove from `pom.xml`:
```xml
<!-- DELETE these if present -->
<dependency>
  <groupId>io.springfox</groupId>
  <artifactId>springfox-boot-starter</artifactId>
</dependency>
```

Delete `SpringFoxConfig.java`.

### 2. Add SpringDoc

Add to `pom.xml`:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```

### 3. Configure in `application.yml`

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
  info:
    title: Discogs Query API
    version: 1.0.0
    description: Batch Discogs record search with rate limiting and caching
```

### 4. Annotate Controller

Add SpringDoc annotations to `DiscogsQueryController`:
```java
@Tag(name = "Discogs Search", description = "Batch search and query the Discogs catalog")
@Operation(summary = "Search Discogs", description = "Process batch queries against the Discogs API")
@ApiResponse(responseCode = "200", description = "Search results")
@ApiResponse(responseCode = "429", description = "Rate limit exceeded")
```

---

## Work Items

- [ ] Remove SpringFox dependency from `pom.xml`
- [ ] Delete `SpringFoxConfig.java`
- [ ] Add SpringDoc dependency
- [ ] Add `springdoc` config to `application.yml`
- [ ] Add `@Tag`, `@Operation`, `@ApiResponse` to `DiscogsQueryController`
- [ ] Verify Swagger UI at `http://localhost:8080/swagger-ui.html` after change
- [ ] Update `docs/API.md` to reflect new Swagger UI path if changed

---

## Acceptance Criteria

- [ ] Application starts without SpringFox-related warnings or errors
- [ ] `http://localhost:8080/swagger-ui.html` shows the API documentation
- [ ] `http://localhost:8080/api-docs` returns valid OpenAPI 3.0 JSON
- [ ] All endpoints documented with at least summary and response codes

---

## Dependencies

None — this is a maintenance fix with no functional changes.
