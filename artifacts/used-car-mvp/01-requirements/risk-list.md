---
artifact_id: REQ-006-risk-list
artifact_type: requirement
package_id: used-car-mvp
version: v1
status: draft
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/PRD.md@v1"
related: []
---

# Risk List — used-car-mvp

| ID | Risk | Likelihood | Impact | Mitigation |
|----|------|------------|--------|------------|
| R-001 | **MySQL ↔ Elasticsearch drift** — listing visible in DB but missing or stale in search | Medium | High | Event-driven sync on publish/update/delete; reconciliation job in demo; AC-004/005/006 verify index lag < 5s |
| R-002 | **Recommendation cold start** — new users see empty or irrelevant suggestions | High | Medium | AC-014 fallback to popular cars; blend 30% popular in AC-013 |
| R-003 | **Scope creep toward full Guazi** — payments, inspection, chat requested mid-build | Medium | High | `out-of-scope.md` explicit; gate design phase against non-goals |
| R-004 | **Dual-store complexity** slows MVP delivery | Medium | Medium | MySQL as sole source of truth; ES read-only for search/recommendations input |
| R-005 | **Session privacy (PII)** — behavior tracking without consent | Low | Medium | Cookie notice in demo UI; no PII in behavior events; document in design |
| R-006 | **Photo upload abuse** — large files or malicious content | Medium | Low | Max file size (e.g. 5MB), image type whitelist, per-listing cap (e.g. 9 photos) |
| R-007 | **Search performance regression** as filters combine | Low | Medium | ES query design review in design phase; index mappings for filter fields; AC-008 p95 target |
| R-008 | **Spring Boot / Next.js version drift** during long build | Low | Low | Pin versions in design; document in `architecture.md` |
