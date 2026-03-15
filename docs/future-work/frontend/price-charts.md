# Price History Charts in Results

**Priority:** P2 — Medium
**Category:** Frontend
**Estimated Effort:** 1–2 days
**Blocked By:** `api-enhancements/price-history.md` (backend data needed)

---

## Context

Once price history data is available from the backend, the results display can
show a price trend sparkline alongside each search result.

---

## Work Items

- [ ] Add lightweight chart library: `recharts` or `chart.js` (avoid full D3 for this scope)
- [ ] Add `PriceSparkline` component to `src/modules/Results/` or `CheapestItem/`
- [ ] Call `GET /discogs-query/releases/{releaseId}/price-history` for each displayed result (with caching)
- [ ] Show 30-day price trend as a small inline sparkline
- [ ] Colour code: green if price trending down, red if up, grey if flat
- [ ] Add "Track this record" button → `POST /discogs-query/watchlist` modal

---

## Acceptance Criteria

- [ ] Sparkline renders for records with price history
- [ ] No sparkline shown (graceful fallback) if no history available
- [ ] Chart does not block initial page render (load lazily)
- [ ] Watchlist modal collects email + target price and submits

---

## Dependencies

- `api-enhancements/price-history.md`
- `api-enhancements/webhook-alerts.md` (for the watchlist form)
