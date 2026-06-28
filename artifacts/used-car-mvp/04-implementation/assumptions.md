---
artifact_id: IMPL-003-assumptions
artifact_type: implementation
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - implements: "artifacts/used-car-mvp/02-design/architecture.md@v1"
  - satisfies: "AC-013"
related: []
---

# Implementation Assumptions & Deviations — used-car-mvp

| ID | Assumption / Deviation | Rationale | Design alignment |
|----|------------------------|-----------|------------------|
| IA-001 | Recommendation candidate scoring runs over MySQL (`findNewestPublished`, pool=200) rather than an ES query | Simpler, robust for demo scale; keeps recommendation logic explainable/testable (A-007). ES remains the search store. | Consistent with design intent (rule-based scoring); section "Recommendation scoring" |
| IA-002 | `viewCount7d` field exists in ES doc but popularity is computed live from `view_events` via SQL aggregation | Avoids a scheduled job for the MVP; fresher counts | data-model noted both options; chose live query |
| IA-003 | Frontend uses package manager **npm** (pnpm not available on build host) | Toolchain availability | No design impact |
| IA-004 | Next.js pinned to **15.5.19** (patched) instead of 15.1.6 | 15.1.6 flagged CVE-2025-66478; stayed on Next 15 line to avoid major upgrade risk | architecture pinned "Next.js 15" |
| IA-005 | Photo upload accepts a **URL** in the publish form rather than binary upload to object storage | Object storage integration deferred; URL-based keeps demo self-contained (A-004) | architecture/object storage simplified for demo |
| IA-006 | Buyer behavior session via HttpOnly `sid` cookie set by backend (also accepts `X-Session-Id`) | Implements A-003 anonymous tracking | data-model `session_id` |
| IA-007 | Recommendation "sparse history" threshold = fewer than 3 combined view+search signals; popular floor = ceil(0.3 * limit) | Concretizes AC-013 "≥30% popular when sparse" | AC-013 |
| IA-008 | Integration tests requiring Testcontainers (ES/MySQL) are specified in the test plan but not added as executable tests in this pass | Docker-dependent; unit tests cover scoring + validation. Captured as follow-up for test-report phase. | test-plan TC-004/005/008 |

## Notes

- No undocumented API or schema changes: endpoints and tables match `api-design.md` and `data-model.md`.
- Recommendations are in-app only (AC-016); no email/notification code exists.
