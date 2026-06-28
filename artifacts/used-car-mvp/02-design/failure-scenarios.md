---
artifact_id: DES-005-failure-scenarios
artifact_type: design
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/risk-list.md@v1"
related:
  - artifacts/used-car-mvp/02-design/architecture.md@v1
---

# Failure Scenarios — used-car-mvp

| ID | Scenario | Detection | Handling / Degradation | Related |
|----|----------|-----------|------------------------|---------|
| FS-001 | ES down during search | ES client timeout/connection error | Return 503 `SEARCH_UNAVAILABLE`; FE shows retry + falls back to newest-from-MySQL listing endpoint if implemented | AC-007, AC-008, R-001 |
| FS-002 | ES sync fails after MySQL commit | Sync event listener exception + log | Enqueue/log failed id; scheduled reconciliation reindex; data still correct in MySQL | AC-004, AC-005, R-001 |
| FS-003 | Listing published in DB but not yet in ES (lag) | Doc absent within 5s window | Acceptable per AC (< 5s); detail page reads MySQL so car is viewable even before indexed | AC-004 |
| FS-004 | Recommendation has no history (cold start) | Empty session history | Return `POPULAR_FALLBACK` (AC-014); never empty/error | AC-014, R-002 |
| FS-005 | Recommendation history too sparse | < N matching candidates | Blend ≥ 30% popular cars to fill slots | AC-013, R-002 |
| FS-006 | Photo upload too large / wrong type | Validation at upload | Reject 400 `INVALID_PHOTO` (max 5MB, jpg/png/webp, ≤ 9 photos) | R-006 |
| FS-007 | Duplicate rapid view events | Same (session, car) < 5 min | Dedupe at write; count once | AC-010 |
| FS-008 | Non-owner edits listing | Ownership check fails | 403 `NOT_OWNER`; audit log | AC-005 |
| FS-009 | Expired/invalid JWT on seller endpoint | Token validation fails | 401 `UNAUTHORIZED`; FE redirects to login | AC-002 |
| FS-010 | MySQL unavailable on write | Connection/timeout | 503; no ES write attempted (AFTER_COMMIT never fires); FE shows error | R-004 |
| FS-011 | search_events table growth | Row count per session | Trim to latest 20 on insert | AC-011 |
| FS-012 | CORS misconfig blocks FE | Browser CORS error | Configurable allowed-origins; documented per env | AC-015 |

## Reliability notes
- All external calls (ES, object storage) have timeouts and are logged.
- Read path (search/recommendation) degrades to popular/newest rather than hard failure where feasible.
- No retries inside DB transaction; ES sync retried out-of-band via reconciliation.
