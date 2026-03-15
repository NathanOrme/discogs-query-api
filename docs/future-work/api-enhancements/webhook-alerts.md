# Price Drop Webhook Alerts

**Priority:** P3 — Low
**Category:** API Enhancements
**Estimated Effort:** 2–3 days
**Blocked By:** `api-enhancements/price-history.md`

---

## Context

Once price history is tracked, users can opt in to receive alerts when a
record they own drops below a target price. Implemented as email notifications
(no push notification complexity needed for V1).

---

## Schema Addition

```sql
CREATE TABLE price_watchlist (
    id           BIGSERIAL   PRIMARY KEY,
    discogs_user VARCHAR(100) NOT NULL,
    release_id   BIGINT      NOT NULL,
    release_title VARCHAR(500),
    target_price NUMERIC(10,2) NOT NULL,
    alert_email  VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT now(),
    last_alerted TIMESTAMP,
    UNIQUE (discogs_user, release_id)
);
```

### Alert Flow

The daily `PriceUpdateSchedulerService` (from price-history) checks after each
price update: if `new_lowest_price < target_price AND last_alerted IS NULL OR last_alerted < 7 days ago`:
send email alert.

### New Endpoints

```
POST /discogs-query/watchlist
  Body: { discogsUser, releaseId, releaseTitle, targetPrice, alertEmail }
  Response: { watchlistId }

GET /discogs-query/watchlist/{discogsUser}
  Response: list of watchlist entries with current price

DELETE /discogs-query/watchlist/{id}
```

---

## Work Items

- [ ] Flyway migration for `price_watchlist` table
- [ ] Add JavaMailSender (or use simple SMTP client) for email alerts
- [ ] Integrate alert check into `PriceUpdateSchedulerService`
- [ ] Create watchlist CRUD controller
- [ ] HTML email template for price drop alert
- [ ] Rate limit alerts: max 1 alert per record per 7 days

---

## Acceptance Criteria

- [ ] User receives email when watched record price drops below target
- [ ] No duplicate alerts within 7-day window
- [ ] Watchlist CRUD works correctly
- [ ] Alert emails include record title, current price, and Discogs marketplace link

---

## Dependencies

- `api-enhancements/price-history.md`
