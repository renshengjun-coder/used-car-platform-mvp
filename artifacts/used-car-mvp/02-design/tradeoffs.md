---
artifact_id: DES-004-tradeoffs
artifact_type: design
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/02-design/architecture.md@v1"
related: []
---

# Tradeoffs — used-car-mvp

## Decision 1: Search store — Elasticsearch vs MySQL full-text

**Options:** (A) MySQL `FULLTEXT` + composite indexes (B) Elasticsearch as derived store
**Chosen:** B (required by stakeholder; also best fit)
**Rationale:** Multi-field filters + keyword relevance + popularity scoring at p95 < 500ms (AC-008) are ES strengths; keeps read load off MySQL.
**Rejected:** A — filter+relevance+sort combinations degrade; harder to tune for recommendations.
**Cost:** dual-store consistency (mitigated R-001/R-004: MySQL source of truth, AFTER_COMMIT sync).

## Decision 2: MySQL→ES sync — AFTER_COMMIT event vs CDC vs dual-write

**Options:** (A) Synchronous dual-write in service (B) Spring `@TransactionalEventListener(AFTER_COMMIT)` (C) Debezium CDC
**Chosen:** B
**Rationale:** No partial-commit dirty index; no infra overhead of CDC for an MVP; meets < 5s lag (AC-004/005/006).
**Rejected:** A — risk of ES write inside DB txn / inconsistency on failure. C — operationally heavy for demo.
**Fallback:** full reindex command for drift recovery.

## Decision 3: Recommendation engine — rule-based vs ML

**Options:** (A) Rule-based weighted scoring (B) collaborative filtering / embeddings
**Chosen:** A (per A-007)
**Rationale:** Explainable, testable (AC-013/014), no training pipeline; fits demo timeline.
**Rejected:** B — cold start, infra, and eval cost unjustified for MVP demo.

## Decision 4: Buyer identity — session cookie vs full buyer auth

**Options:** (A) Anonymous `session_id` cookie (B) mandatory buyer accounts
**Chosen:** A (per A-002/A-003)
**Rationale:** Enables behavior tracking + recommendations without buyer auth scope; lowers friction.
**Rejected:** B — out of MVP scope; adds auth surface and PII.

## Decision 5: Auth tokens — JWT vs server sessions

**Options:** (A) Stateless JWT (B) server-side session store
**Chosen:** A
**Rationale:** Simple FE/BE separation, no shared session store needed for demo.
**Rejected:** B — extra infra (Redis) for MVP; revocation not a demo requirement.

## Decision 6: Rendering — Next.js Server Components vs full client SPA

**Options:** (A) Server Components for list/detail + client islands (B) client-only SPA
**Chosen:** A (aligns with "prefer server components" rule)
**Rationale:** Better first-load/SEO for public marketplace pages; data fetch on server.
**Rejected:** B — worse SEO and initial load for content pages.
