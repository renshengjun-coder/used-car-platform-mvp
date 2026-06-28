---
artifact_id: TR-004-e2e-test-report
artifact_type: test-report
package_id: used-car-mvp
version: v2
status: complete
owner: ""
created_at: 2026-06-28
updated_at: 2026-06-28
traces:
  - validates: "AC-016"
related: []
---

# E2E Test Report — used-car-mvp

**Status: PASS — 3/3 Playwright tests green.**

| Field | Value |
|-------|-------|
| Tool | Playwright `@playwright/test` 1.55.1, Chromium (headless) |
| Stack | backend `mvn spring-boot:run` (seeded demo data) + frontend `next dev`, datastores on localhost |
| Command | `playwright test` · `E2E_BASE_URL=http://localhost:3000` |
| Result | `3 passed (28.7s)` |

## Cases (TC-016 → AC-016)

| Test | Assertion | Result |
|------|-----------|--------|
| `listing page shows a recommendation strip at the top` | First `section` on `/` renders a "Recommended for you / Popular right now" heading with ≥1 car link | ✓ pass |
| `car detail page shows 'You may also like' recommendations` | Opening a listing navigates to `/cars/{id}` and renders a "You may also like" heading | ✓ pass |
| `recommendations are in-app only (no outbound email links)` | No `a[href^="mailto:"]` anchors exist | ✓ pass |

This confirms AC-016: recommendations are surfaced **in-app** at the top of the listing page and
within the car detail page, with **no outbound email** channel.

## Setup notes

- `@playwright/test` was bumped from 1.49.1 → **1.55.1** to satisfy `next@15.5.19`'s peer
  requirement (`^1.51.1`); the matching Chromium build was installed via the local Playwright binary.
- Tests were run with the local `node_modules/.bin/playwright` (an `npx` invocation resolved a
  cached Playwright that could not load the local `@playwright/test` config import).
