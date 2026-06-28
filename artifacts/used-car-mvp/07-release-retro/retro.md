---
artifact_id: REL-003-retro
artifact_type: release
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/06-test-report/test-execution-summary.md@v2"
related: []
---

# Retrospective — used-car-mvp

**Date:** 2026-06-28 · **Profile:** standard · **Outcome:** MVP demo shipped (GO)

## What went well

- **End-to-end traceability held:** all 16 ACs mapped from requirements → design → test plan → code → executed tests.
- **Greenfield velocity:** full-stack MVP (Spring Boot + Next.js + MySQL + ES + Docker Compose) delivered in a single devloop package.
- **Test pyramid caught a real bug:** the timezone mismatch (DEF-001) would have silently broken dedup and popularity on non-UTC servers — found during integration testing, not in review.
- **Code review was clean:** 0 blocking issues across five review lenses before test execution.
- **Requirements clarification early:** in-app-only recommendations (US-009 / AC-016) resolved before design, avoiding rework.

## What to improve

- **Environment dependencies blocked validation initially:** Docker daemon was down during the first test-report pass, producing a honest but incomplete NO-GO. CI should provision datastores automatically so integration/E2E never depend on manual Docker startup.
- **Testcontainers ↔ Docker Engine version fragility:** Engine 29 broke the bundled docker-java client; we worked around it with CLI-provisioned services. Pin Docker/testcontainers versions in CI or add a health preflight.
- **Performance validation deferred:** AC-008 functional pass is not the same as the p95 latency target; large-seed probes should be part of the standard profile for search-heavy features.
- **No line-coverage tooling:** behavioral AC coverage is complete, but JaCoCo would give earlier signal on untested branches.

## Action items

| # | Action | Owner | Priority |
|---|--------|-------|----------|
| 1 | Add CI job: `docker compose up -d mysql elasticsearch` → `mvn verify` → `playwright test` on every PR | TBD | High |
| 2 | Add JaCoCo to backend pom with a minimum coverage threshold for new code | TBD | Medium |
| 3 | Run TC-008b large-seed perf probe and document baseline p95 before any production traffic | TBD | Medium |
| 4 | Automate dual-store failure-path test (ES down → graceful degradation + reindex) | TBD | Medium |
