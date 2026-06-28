---
artifact_id: TP-004-regression
artifact_type: test-plan
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/03-test-plan/test-cases.md@v1"
related: []
---

# Regression / Smoke Set — used-car-mvp

Run on every CI build once implementation exists.

## Smoke (must pass to deploy demo)

| ID | Flow | Maps to |
|----|------|---------|
| SM-001 | BE health + OpenAPI reachable | AC-015 |
| SM-002 | Seller register → login → publish listing | TC-001/002/003 |
| SM-003 | Published listing appears in search ≤ 5s | TC-004 |
| SM-004 | Browse list + apply 3 filters returns correct set | TC-007/008 |
| SM-005 | Car detail loads with photos; 404 for unpublished | TC-009 |
| SM-006 | View/search events recorded | TC-010/011 |
| SM-007 | Popular endpoint returns cars (with fallback) | TC-012 |
| SM-008 | Recommendations render at list top + detail section | TC-016 |
| SM-009 | Cold-start recommendation falls back to popular | TC-014 |

## Full regression (per release)

- All TC-001–TC-016 + EC-001–EC-017.
- Performance smoke TC-008b on seeded 10k dataset.

## CI ordering

1. Unit (FE + BE) → 2. BE integration (Testcontainers) → 3. Contract → 4. E2E (Playwright) → 5. Perf smoke (nightly/manual).
