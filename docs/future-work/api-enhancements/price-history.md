# Price History Tracking

**Priority:** P2 — Medium
**Category:** API Enhancements
**Estimated Effort:** 2–3 days
**Blocked By:** `api-enhancements/collection-sync.md` (need to know which records to track)

---

## Context

Once a user's collection is stored, we can periodically poll Discogs marketplace
prices and record history. This enables price trend charts in the frontend.

---

## Schema Addition

```sql
CREATE TABLE price_history (
    id            BIGSERIAL   PRIMARY KEY,
    release_id    BIGINT      NOT NULL,  -- Discogs release ID
    recorded_at   TIMESTAMP   NOT NULL DEFAULT now(),
    lowest_price  NUMERIC(10,2),
    median_price  NUMERIC(10,2),
    num_listings  INTEGER
);

CREATE INDEX idx_price_history_release_date ON price_history(release_id, recorded_at DESC);
```

### Scheduled Job

```java
@Scheduled(cron = "0 0 2 * * *")  // 2 AM daily
public void updatePricesForAllCollections() {
    // For each unique release_id across all synced collections:
    // call Discogs marketplace API, store new price_history row
    // update collection_releases.lowest_price + price_last_updated
}
```

### New Endpoint

```
GET /discogs-query/releases/{releaseId}/price-history
  Response: { releaseId, history: [ { date, lowestPrice, medianPrice, numListings } ] }
  Query: ?from=2024-01-01&to=2024-12-31
```

---

## Work Items

- [ ] Write Flyway migration for `price_history` table
- [ ] Create `PriceHistoryEntity` + `PriceHistoryRepository`
- [ ] Create `PriceUpdateSchedulerService` with `@Scheduled` job
- [ ] Add `spring.task.scheduling.enabled=true` to `application.yml`
- [ ] Rate limit the scheduled job against the 60 req/min Discogs limit
- [ ] Create `GET /discogs-query/releases/{releaseId}/price-history` endpoint
- [ ] Write unit test for price history retrieval with date filtering

---

## Acceptance Criteria

- [ ] Price history job runs daily without hitting Discogs rate limit
- [ ] `GET /discogs-query/releases/{id}/price-history` returns chronologically ordered history
- [ ] Date range filtering returns only the requested window
- [ ] Scheduling can be disabled via `price-scheduler.enabled=false` config property

---

## Dependencies

- `api-enhancements/collection-sync.md`
