# Docker Compose — Local Development

**Priority:** P1 — High
**Category:** Infrastructure
**Estimated Effort:** 0.5 day
**Status:** Dockerfile exists; no compose for local dev

---

## Context

Running locally requires manual PostgreSQL setup (once collection-sync is added)
and env var configuration. A Docker Compose file makes this a one-command setup.

---

## Target `docker-compose.yml`

```yaml
version: '3.9'

services:
  discogs-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DISCOGS_API_TOKEN=${DISCOGS_API_TOKEN}
      - DATABASE_URL=jdbc:postgresql://postgres:5432/discogsquery
      - DATABASE_USERNAME=discogsquery
      - DATABASE_PASSWORD=discogsquery_local
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - discogs-net

  postgres:
    image: postgres:16-alpine
    ports:
      - "5433:5432"   # Use 5433 to avoid conflict with Theboot's 5432
    environment:
      POSTGRES_DB: discogsquery
      POSTGRES_USER: discogsquery
      POSTGRES_PASSWORD: discogsquery_local
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U discogsquery"]
      interval: 5s
      timeout: 5s
      retries: 5
    networks:
      - discogs-net

networks:
  discogs-net:
```

Note: **PostgreSQL is only needed once `collection-sync` feature is added.** For
the current stateless service, the compose can just run the API container.

## `.env.example`

```bash
DISCOGS_API_TOKEN=your_discogs_personal_access_token_here
```

---

## Work Items

- [ ] Create `docker-compose.yml`
- [ ] Create `.env.example`
- [ ] Add `.env` to `.gitignore`
- [ ] Update `README.md` with compose quick-start instructions
- [ ] Verify `http://localhost:8080/swagger-ui.html` accessible after `docker compose up`

---

## Acceptance Criteria

- [ ] `docker compose up` starts the API in under 30 seconds
- [ ] Swagger UI accessible at `http://localhost:8080/swagger-ui.html`
- [ ] `http://localhost:8080/actuator/health` returns UP

---

## Dependencies

None for the current stateless version. PostgreSQL service needed once `api-enhancements/collection-sync.md` is implemented.
