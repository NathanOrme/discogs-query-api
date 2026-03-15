# Mobile Improvements

**Priority:** P2 — Medium
**Category:** Frontend
**Estimated Effort:** 1–2 days

---

## Context

The stepper-based search workflow was designed for desktop. On mobile, the
multi-step form and results table need responsive treatment.

---

## Work Items

- [ ] Audit current layout on 375px (iPhone SE) — document issues
- [ ] Stepper: collapse to a single-column vertical stepper on mobile
- [ ] Search form fields: full-width stack below `sm` breakpoint
- [ ] Results: switch from table view to card view on mobile (one card per record)
- [ ] Touch targets: all buttons/links minimum 44×44px
- [ ] Font sizes: minimum 16px on inputs to prevent iOS zoom
- [ ] "Cheapest Item" panel: full-width on mobile

---

## Acceptance Criteria

- [ ] No horizontal scroll on any screen ≤ 428px
- [ ] Stepper navigation works with touch (swipe or tap buttons)
- [ ] Results cards are readable on 375px without zooming
- [ ] Lighthouse mobile score ≥ 80

---

## Dependencies

- `frontend/ui-library-migration.md` — easier to do responsive work after Tailwind migration
