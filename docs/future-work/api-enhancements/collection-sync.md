# Discogs Collection Sync Endpoint

**Priority:** P2 — Medium
**Category:** API Enhancements
**Estimated Effort:** 3–5 days
**Status:** Not started — service is currently stateless (no DB)

---

## Context

The service currently only does search (stateless). To support the portfolio
frontend's rich collection browser, it needs to accept a Discogs OAuth token
and sync the user's collection to a local PostgreSQL database for fast, indexed
querying without hitting Discogs rate limits on every page load.

---

## Adding a Database

This service is currently stateless. To add collection sync, a PostgreSQL
database is needed:

1. Add `spring-boot-starter-data-jpa` + `postgresql` driver to `pom.xml`
2. Add Flyway for schema management
3. See `infrastructure/docker-compose.md` for adding PostgreSQL to local dev

### Schema

```sql
-- Synced Discogs collection for a user identity
CREATE TABLE discogs_collections (
    id           BIGSERIAL    PRIMARY KEY,
    discogs_user VARCHAR(100) NOT NULL,
    synced_at    TIMESTAMP    NOT NULL DEFAULT now(),
    release_count INTEGER
);

CREATE TABLE collection_releases (
    id                BIGSERIAL    PRIMARY KEY,
    collection_id     BIGINT       NOT NULL REFERENCES discogs_collections(id) ON DELETE CASCADE,
    release_id        BIGINT       NOT NULL,
    title             VARCHAR(500),
    artists           VARCHAR(500),
    year              INTEGER,
    formats           VARCHAR(255),
    labels            VARCHAR(500),
    genres            VARCHAR(500),
    styles            VARCHAR(500),
    lowest_price      NUMERIC(10,2),
    median_price      NUMERIC(10,2),
    price_last_updated TIMESTAMP,
    discogs_url       VARCHAR(500),
    thumb_url         VARCHAR(500)
);

CREATE INDEX idx_collection_releases_collection ON collection_releases(collection_id);
CREATE INDEX idx_collection_releases_release ON collection_releases(release_id);
```

### New Endpoints

```
POST /discogs-query/collection/sync
  Body: { discogsUsername, oauthToken, oauthTokenSecret }
  Returns: { collectionId, releaseCount, syncedAt }
  Note: Respects Discogs rate limit (60 req/min via existing RateLimiterService)

GET /discogs-query/collection/{collectionId}
  Returns: paginated list of CollectionReleaseDTO
  Query params: ?page=0&size=24&genre=Jazz&decade=1970s&sort=artist

GET /discogs-query/collection/{collectionId}/stats
  Returns: { totalReleases, genreBreakdown, decadeBreakdown, avgPrice, mostExpensive }
```

---

## Work Items

- [ ] Add JPA + PostgreSQL dependencies to `pom.xml`
- [ ] Add Flyway + migration file for collection tables
- [ ] Create `DiscogsCollectionEntity`, `CollectionReleaseEntity` JPA entities
- [ ] Create `DiscogsCollectionSyncService` — fetch collection from Discogs API page by page
- [ ] Reuse existing `RateLimiterServiceImpl` for rate-limited Discogs calls
- [ ] Add price enrichment: for each release, also call cheapest listing endpoint
- [ ] Create collection controller with sync + retrieval endpoints
- [ ] Add pagination support to `GET /discogs-query/collection/{id}`
- [ ] Write integration test with mock Discogs API (WireMock)

---

## Acceptance Criteria

- [ ] Sync 500-record collection in < 5 minutes (rate-limited at 60 req/min)
- [ ] Paginated collection retrieval returns correct page with metadata
- [ ] Genre and decade filters work correctly
- [ ] Re-syncing updates existing records (upsert, not duplicate insert)

---

## Dependencies

- `infrastructure/docker-compose.md` (PostgreSQL needed for local dev)
- Frontend: `../rgbnathan-portfolio/docs/future-work/needs-backend/discogs-collection-management.md`
