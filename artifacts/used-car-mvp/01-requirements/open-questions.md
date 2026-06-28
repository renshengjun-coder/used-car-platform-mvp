---
artifact_id: REQ-005-open-questions
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

# Open Questions — used-car-mvp

All items resolved or converted to assumptions before archive.

| ID | Question | Resolution | Date |
|----|----------|------------|------|
| OQ-001 | What MVP scope beyond Guazi inspiration? | **Resolved** — Three scenarios: seller publish, browse/filter search, behavior-based recommendations (user input 2026-06-28) | 2026-06-28 |
| OQ-002 | What tech stack? | **Resolved** — FE: TS + Tailwind + Next.js; BE: Spring Boot + MySQL + MyBatis; ES for search (user input 2026-06-28) | 2026-06-28 |
| OQ-003 | Geographic market? | **Assumption A-001** — Single country, China-style fields (Guazi reference + prior single-country selection) | 2026-06-28 |
| OQ-004 | Buyer authentication required? | **Assumption A-002/A-003** — Sellers auth required; buyers anonymous with session tracking | 2026-06-28 |
| OQ-005 | Recommendation approach? | **Assumption A-007** — Rule-based scoring for MVP demo | 2026-06-28 |
| OQ-006 | Photo storage backend? | **Assumption A-004** — Local or S3-compatible object storage | 2026-06-28 |

**Status:** All questions closed — none blocking archive.
