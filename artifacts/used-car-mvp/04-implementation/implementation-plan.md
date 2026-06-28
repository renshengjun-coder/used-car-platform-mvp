---
artifact_id: IMPL-001-plan
artifact_type: implementation
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - implements: "artifacts/used-car-mvp/02-design/architecture.md@v1"
  - satisfies: "AC-001"
  - satisfies: "AC-002"
  - satisfies: "AC-003"
  - satisfies: "AC-004"
  - satisfies: "AC-005"
  - satisfies: "AC-006"
  - satisfies: "AC-007"
  - satisfies: "AC-008"
  - satisfies: "AC-009"
  - satisfies: "AC-010"
  - satisfies: "AC-011"
  - satisfies: "AC-012"
  - satisfies: "AC-013"
  - satisfies: "AC-014"
  - satisfies: "AC-015"
  - satisfies: "AC-016"
related: []
---

# Implementation Plan — used-car-mvp

## Repository layout

```
used-car-platform-mvp/
├── backend/                 # Spring Boot (Java 21, Maven)
│   ├── pom.xml
│   └── src/main/java/com/usedcar/...
│   └── src/main/resources/  (application.yml, schema.sql, mapper XML)
│   └── src/test/java/...
├── frontend/                # Next.js (App Router, TS, Tailwind)
│   ├── package.json
│   └── src/app, src/components, src/lib
├── docker-compose.yml       # MySQL 8 + Elasticsearch 8
└── README.md
```

## Task → AC map

| Task | Files | AC |
|------|-------|-----|
| T1 BE scaffold | pom.xml, Application.java, application.yml | AC-015 |
| T2 Config (Security/JWT/CORS/ES/OpenAPI) | config/* | AC-001,002,015 |
| T3 Auth module | controller/AuthController, service/AuthService, JwtService, mapper SellerMapper | AC-001, AC-002 |
| T4 Listing module | ListingController, ListingService, ListingMapper(+XML), entities, DTOs | AC-003,005,006,009 |
| T5 ES sync | EsListingDocument, EsSyncService, ListingSearchRepository | AC-004,005,006 |
| T6 Search module | SearchController, SearchService (ES query builder) | AC-007, AC-008 |
| T7 Behavior module | BehaviorController, BehaviorService, ViewEventMapper, SearchEventMapper | AC-010, AC-011 |
| T8 Recommendation module | RecommendationController, RecommendationService (scoring), PopularService | AC-012,013,014,016 |
| T9 Schema + seed | schema.sql, data seeding component | all |
| T10 FE scaffold | package.json, next/tailwind/ts config, lib/api | AC-015 |
| T11 FE pages | app/page (list+rec strip top), app/cars/[id] (rec section), app/sell | AC-007,008,009,016 |
| T12 FE components | CarCard, FilterBar, RecommendationStrip, PhotoGallery, behavior hooks | AC-008,010,011,016 |
| T13 Infra | docker-compose, README, env examples | deploy |
| T14 Tests | BE unit (scoring, validation), integration (Testcontainers); FE component; E2E placeholder | test-plan |

## Test plan (this phase)

- BE: `mvn test` — unit tests for recommendation scoring + listing validation; integration test stubs using Testcontainers (require Docker).
- FE: `pnpm test` — component test for RecommendationStrip placement.
- Build verification: `mvn -q compile` (BE), `pnpm build`/`tsc --noEmit` (FE) where toolchain available.

## Rollback approach

- All new code under `backend/` and `frontend/` directories; rollback = remove dirs / revert commit.
- No existing files modified except adding to repo root (docker-compose, README). Devloop `.ai/` artifacts unaffected.

## Constraints honored

- FE/BE separation; no business logic in UI components (scoring in BE).
- Server Components for list/detail; client islands for forms/filters.
- MySQL source of truth; ES derived; AFTER_COMMIT sync.
- Recommendations in-app only (AC-016), displayed list-top + detail.
