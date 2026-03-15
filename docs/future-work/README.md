# Future Work — discogs-query-api

Specialized Discogs search backend. Spring Boot 3.5.4 / Java 24.
Primary endpoint: `POST /discogs-query/search` (batch search with rate limiting + Caffeine cache).

**Related repos:**
- Frontend: `../rgbnathan-portfolio` — consumes this service via DiscogsPage
- Primary backend: `../Theboot-1001-albums` — also has some Discogs proxy endpoints

---

## Priority Legend

| Label | Meaning |
|---|---|
| P0 — Critical | Blocking or causing active issues |
| P1 — High | Important maintenance or significant value |
| P2 — Medium | Valuable enhancement |
| P3 — Low | Nice to have |

---

## Quick Reference

### [api-enhancements/](./api-enhancements/)

| Document | Summary | Priority |
|---|---|---|
| [springdoc-migration.md](./api-enhancements/springdoc-migration.md) | Remove deprecated SpringFox, use SpringDoc | P1 |
| [collection-sync.md](./api-enhancements/collection-sync.md) | Store user Discogs collection in DB | P2 |
| [price-history.md](./api-enhancements/price-history.md) | Track price changes over time | P2 |
| [webhook-alerts.md](./api-enhancements/webhook-alerts.md) | Price drop notifications | P3 |

### [frontend/](./frontend/)

| Document | Summary | Priority |
|---|---|---|
| [ui-library-migration.md](./frontend/ui-library-migration.md) | Material-UI → shadcn/ui + Tailwind | P2 |
| [price-charts.md](./frontend/price-charts.md) | Price trend sparklines in results | P2 |
| [mobile-improvements.md](./frontend/mobile-improvements.md) | Mobile-first stepper and results | P2 |

### [infrastructure/](./infrastructure/)

| Document | Summary | Priority |
|---|---|---|
| [spring-boot-upgrade.md](./infrastructure/spring-boot-upgrade.md) | Align with Theboot on Spring Boot 4.x | P2 |
| [docker-compose.md](./infrastructure/docker-compose.md) | Local dev compose with DB | P1 |

### [testing/](./testing/)

| Document | Summary | Priority |
|---|---|---|
| [coverage-improvements.md](./testing/coverage-improvements.md) | 80%+ coverage target | P1 |

---

## Suggested Work Order

1. **Maintenance** (no deps): `api-enhancements/springdoc-migration`, `infrastructure/docker-compose`
2. **Testing**: `testing/coverage-improvements`
3. **New features**: `api-enhancements/collection-sync` → `api-enhancements/price-history` → `frontend/price-charts`
4. **Infrastructure**: `infrastructure/spring-boot-upgrade`
5. **Frontend improvements**: `frontend/ui-library-migration`, `frontend/mobile-improvements`
