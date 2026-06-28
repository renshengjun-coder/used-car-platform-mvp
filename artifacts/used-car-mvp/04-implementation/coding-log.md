---
artifact_id: IMPL-004-coding-log
artifact_type: implementation
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - implements: "artifacts/used-car-mvp/02-design/architecture.md@v1"
  - satisfies: "AC-015"
related: []
---

# Coding Log — used-car-mvp

## 2026-06-28

### 1. Backend scaffold + config
- Files: `pom.xml`, `application.yml`, `UsedCarApplication.java`, `config/*`, `common/*`.
- Spring Boot 3.3.5, Java 21; security (JWT stateless), CORS, springdoc OpenAPI, MyBatis, Spring Data Elasticsearch.

### 2. Domain, mappers, schema
- Entities + MyBatis mapper interfaces and XML for sellers, listings, photos, view/search events.
- `schema.sql` auto-applied on startup (spring.sql.init).

### 3. Modules
- Auth (register/login, BCrypt, JWT), Listing (CRUD + ownership + photo rules + AFTER_COMMIT ES events), Search (ES criteria query + pagination), Behavior (view dedupe 5min, search trim-to-20), Recommendation (PopularService 7-day + newest fallback; RecommendationService rule-based scoring, cold-start fallback, popular floor for sparse history).
- `EsSyncService` upserts/removes ES docs after DB commit; `reindexAll()` for drift recovery.
- `DataSeeder` seeds demo seller + 12 listings and indexes to ES.

### 4. Build & tests (backend)
- Command: `mvn -q -DskipTests compile`
  - Result: **PASS** (exit 0).
- Command: `mvn -q test`
  - Result: **PASS** (exit 0). Tests: `RecommendationServiceTest` (cold-start → POPULAR_FALLBACK), `ListingServiceTest` (publish-without-photo rejected, too-many-photos rejected).

### 5. Frontend
- Next.js 15 App Router (TS + Tailwind). Server components for list/detail; client components for FilterBar, RecommendationStrip, ViewTracker, sell form.
- Recommendation strip rendered at TOP of list page and as "You may also like" on detail page (AC-016).
- Typed API client in `src/lib/api.ts`.

### 6. Build & checks (frontend)
- Command: `npm install`
  - Result: **PASS**; flagged `next@15.1.6` CVE-2025-66478.
- Command: `npm run typecheck` (`tsc --noEmit`)
  - Result: **PASS** (exit 0).
- Command: `npm run build`
  - Result: **PASS** — 4 routes compiled (`/`, `/cars/[id]`, `/sell`, `/_not-found`).
- Remediation: bumped Next to **15.5.19** (patched), reran `npm install` + `npm run build` → **PASS**. Remaining 2 moderate transitive advisories noted (non-critical).

### 7. Lint
- `ReadLints` on `frontend/src` and `backend/src/main/java`: **No linter errors found.**

### 8. Infra
- `docker-compose.yml` (MySQL 8.4, Elasticsearch 8.13, backend, frontend), Dockerfiles, README.

## Outstanding (for test-report phase)
- Testcontainers integration tests (TC-004/005/006/008) and Playwright E2E (TC-016) require Docker runtime; specified in test-plan, not executed in this pass (IA-008).
- Performance smoke (TC-008b) needs seeded 10k dataset.
