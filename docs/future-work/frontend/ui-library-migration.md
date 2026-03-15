# UI Library Migration — Material-UI → shadcn/ui + Tailwind

**Priority:** P2 — Medium
**Category:** Frontend
**Estimated Effort:** 3–5 days
**Status:** Current: Material-UI (@mui/material) with MUI theming

---

## Context

The embedded frontend (`src/main/frontend/`) uses Material-UI. The main portfolio
frontend uses shadcn/ui + Tailwind CSS v4. Migrating aligns the design language,
reduces cognitive overhead when working across both frontends, and shrinks the
bundle (Tailwind tree-shakes aggressively; MUI has a larger baseline).

---

## Current Component Inventory

The embedded frontend has relatively few components:
- `QueryFields` — form fields for search parameters
- `SearchForm` — main search form with stepper navigation
- `Results` — results list/grid
- `CheapestItem` — individual result card

---

## Migration Strategy

Since this is a small app, a full rewrite is feasible:

1. Add Tailwind CSS v4 to `src/main/frontend/`
2. Install shadcn/ui CLI: `npx shadcn@latest init`
3. Add needed components: Button, Input, Card, Badge, Table, Select, Stepper (or custom)
4. Replace MUI components one-by-one:
   - `<TextField>` → `<Input>` + `<Label>` (shadcn)
   - `<Button>` → `<Button>` (shadcn)
   - `<Card>` → `<Card>` (shadcn)
   - MUI Stepper → custom step indicator with Tailwind
   - MUI Typography → Tailwind prose classes
5. Remove `@mui/material` and `@emotion/*` from `package.json`

---

## Work Items

- [ ] Audit all MUI component usages in `src/main/frontend/src/`
- [ ] Add Tailwind CSS v4 to Vite config
- [ ] Install shadcn/ui
- [ ] Migrate `QueryFields` component
- [ ] Migrate `SearchForm` with stepper
- [ ] Migrate `Results` list
- [ ] Migrate `CheapestItem` card
- [ ] Remove MUI dependencies
- [ ] Verify bundle size decrease

---

## Acceptance Criteria

- [ ] All existing functionality works after migration
- [ ] No MUI imports remain
- [ ] Bundle size decreases (measure with `vite-bundle-visualizer`)
- [ ] Matches the glass-morphism dark theme of the main portfolio frontend

---

## Dependencies

None — this is a self-contained frontend change.
