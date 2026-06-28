---
artifact_id: REL-002-known-issues
artifact_type: release
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/06-test-report/defects.md@v2"
related: []
---

# Known Issues — used-car-mvp v0.1.0

**As of:** 2026-06-28

## Shipped-with limitations (non-blocking for MVP demo)

| ID | Severity | Issue | Workaround / follow-up |
|----|----------|-------|------------------------|
| GAP-003 | medium | Search p95 latency target (AC-008, < 500 ms at 10k listings) not load-tested | Functional filtering verified; run k6/JMeter with large seed before scaling beyond demo data |
| GAP-004 | low | No JaCoCo line/branch coverage instrumentation | Add `jacoco-maven-plugin` before production release |
| GAP-005 | medium | Dual-store (MySQL→ES) failure/retry paths not automated | Monitor ES health; manual reindex via `EsSyncService.reindexAll()` if sync drift occurs |
| ENV-001 | low | Host port 3306 may be unavailable on some Docker Desktop setups | Use alternate host port (e.g. 3307) via `MYSQL_PORT` env var |
| ENV-002 | low | Testcontainers docker-java client incompatible with Docker Engine 29 on some hosts | Integration tests run against CLI-provisioned datastores; revisit when docker-java supports Engine 29 |

## Resolved in this release

| ID | Issue | Resolution |
|----|-------|------------|
| DEF-001 | View-dedup and popularity windows broken on non-UTC hosts | JVM pinned to UTC in `UsedCarApplication` |

## Explicit "none" for critical/blocking defects

No open **blocking** functional defects at release time. All 16 acceptance criteria pass in executed tests.
