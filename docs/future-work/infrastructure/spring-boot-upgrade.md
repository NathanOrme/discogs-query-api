# Spring Boot 4.x Upgrade

**Priority:** P2 — Medium
**Category:** Infrastructure
**Estimated Effort:** 1–3 days
**Current:** Spring Boot 3.5.4 / Java 24
**Target:** Spring Boot 4.x / Java 21+ (align with Theboot)

---

## Context

The Theboot backend is on Spring Boot 4.0.2. Keeping this service on 3.x means:
- Different security model (Spring Security 7 in Boot 4 vs 6 in Boot 3)
- Different dependency versions to manage
- Can't share code/patterns directly between services

## Key Breaking Changes (3.x → 4.x)

1. **Spring Security**: Removed some deprecated APIs, updated default CSRF config
2. **Spring Data**: Minor API changes to `Page` and repository return types
3. **Actuator**: Some endpoint path changes
4. **Auto-configuration**: Some `@ConditionalOn*` classes moved packages
5. **Jakarta EE**: Already migrated in 3.x (no change needed)

---

## Work Items

- [ ] Bump `spring-boot-starter-parent` to `4.0.x` in `pom.xml`
- [ ] Run `./mvnw test` — identify all compilation errors
- [ ] Fix any Spring Security config issues (SecurityFilterChain remains the pattern)
- [ ] Check `CacheConfig.java` — Caffeine config API is unchanged
- [ ] Check `HttpConfig.java` — RestClient (Spring 6.1+) preferred over RestTemplate
- [ ] Run full test suite — fix any runtime failures
- [ ] Verify Swagger UI still works (SpringDoc 2.x supports Boot 4)
- [ ] Also do `api-enhancements/springdoc-migration.md` at the same time

---

## Acceptance Criteria

- [ ] All tests pass on Spring Boot 4.x
- [ ] `./mvnw spring-boot:run` starts without errors
- [ ] Swagger UI accessible
- [ ] No deprecation warnings for APIs used in this codebase

---

## Dependencies

- Best done alongside `api-enhancements/springdoc-migration.md`
