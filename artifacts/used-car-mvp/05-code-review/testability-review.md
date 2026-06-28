---
artifact_id: CR-005-testability-review
artifact_type: code-review
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - reviews: "artifacts/used-car-mvp/04-implementation/changed-files.md@v1"
related: []
---

# Testability Review — used-car-mvp

| ID | Severity | Location | Finding | Suggested test |
|----|----------|----------|---------|----------------|
| TST-001 | non-blocking | search/SearchService | No automated test for filter ANDing + pagination (TC-007/008). Requires ES. | `SearchServiceIT` (Testcontainers ES): seed docs, assert filter combination + page size 20 |
| TST-002 | non-blocking | search/es/EsSyncService | No test for publish→index lag and unpublish→removal (TC-004/006). | `EsSyncIT`: publish then poll index < 5s; unpublish removes doc |
| TST-003 | non-blocking | behavior/BehaviorService | Dedupe (5 min) and trim-to-20 logic untested at integration level (TC-010/011). | `BehaviorServiceIT` (Testcontainers MySQL): double view deduped; 25 searches → 20 retained |
| TST-004 | non-blocking | recommendation/RecommendationService | Personalized path (attr overlap, exclude viewed, ≥30% popular) untested; only cold start unit-tested (TC-013). | `RecommendationServiceIT`: seed history, assert overlap ranking + exclusions + popular floor |
| TST-005 | non-blocking | frontend rec placement | AC-016 placement (list top + detail section) not covered by automated E2E (TC-016). | Playwright: assert top strip on `/` and "You may also like" on `/cars/[id]`; assert no outbound email/network notification |

## Positives

- Services are constructor-injected and mockable; unit tests already cover scoring cold start and listing photo validation.
- Clear failure codes (`ApiException` with stable codes) ease assertion.
- Behavior/dedupe windows and retention are deterministic and testable.

## Observability

- Logging present on ES sync failure (FS-002) and search failure (FS-001). Consider adding metrics/counters for sync lag in a later phase.

**Blocking testability findings:** 0 (integration/E2E gaps are tracked for test-report execution, consistent with implementation IA-008; not blocking code-review since tests are planned and code is structured for them.)
