---
artifact_id: TR-006-defects
artifact_type: test-report
package_id: used-car-mvp
version: v2
status: complete
owner: ""
created_at: 2026-06-28
updated_at: 2026-06-28
traces:
  - validates: "AC-001"
  - validates: "AC-010"
  - validates: "AC-016"
related: []
---

# Defects & Validation Gaps — used-car-mvp

Full suite executed: 3 unit + 9 integration + 3 E2E, **0 failures**. One real product defect was
found and fixed during validation; the previous environment-driven validation gaps are now closed.

## Defects found & resolved

| ID | Type | Severity | Status | AC | Description | Fix |
|----|------|----------|--------|----|-------------|-----|
| DEF-001 | code defect | high | **fixed** | AC-010, AC-012 | Dedup/recency windows were built from host-local `LocalDateTime.now()` while rows were written with DB-side `NOW()` (UTC). On a non-UTC host the `created_at >= since` comparison never matched, silently disabling view-dedup and time-windowed popularity. | Pin JVM to UTC in `UsedCarApplication` (`@PostConstruct` + `main`). Re-verified by `BehaviorRecommendationIT.view_dedupes_within_window`. |

## Validation gaps (previously open) — now closed

| ID | Prior status | Resolution |
|----|--------------|------------|
| GAP-001 | open (integration skipped, Docker down) | **closed** — 9/9 integration tests pass (`mvn verify`) against CLI-provisioned MySQL+ES |
| GAP-002 | open (E2E skipped, no stack) | **closed** — 3/3 Playwright E2E pass against the running stack (AC-016) |

## Remaining gaps (non-blocking)

| ID | Type | Severity | Status | AC | Action |
|----|------|----------|--------|----|--------|
| GAP-003 | validation-gap | medium | open | AC-008 | Large-seed (10k) search-latency probe (TC-008b) deferred; run k6/JMeter before scaling |
| GAP-004 | validation-gap | low | open | all | Add JaCoCo for line/branch coverage numbers |
| GAP-005 | validation-gap | medium | open | R-001 | Automate dual-store (ES/broker outage) failure-path tests |

Transition history:
- DEF-001 found & fixed 2026-06-28 (timezone window bug).
- GAP-001, GAP-002 closed 2026-06-28 (integration + E2E executed and green).
- GAP-003/004/005 remain as documented, non-blocking, post-MVP follow-ups.
