---
artifact_id: IMPL-002-changed-files
artifact_type: implementation
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - implements: "artifacts/used-car-mvp/02-design/architecture.md@v1"
  - satisfies: "AC-001"
  - satisfies: "AC-016"
related: []
---

# Changed Files — used-car-mvp

Change type: `add` unless noted. Root: repository root.

## Backend (Spring Boot)

| File | AC / Design |
|------|-------------|
| backend/pom.xml | AC-015 (deps: web, security, validation, data-elasticsearch, mybatis, jjwt, springdoc) |
| backend/Dockerfile | deploy |
| backend/.gitignore | — |
| backend/src/main/resources/application.yml | AC-015; config (jwt, cors, recommendation weights) |
| backend/src/main/resources/schema.sql | data-model (all ACs) |
| backend/src/main/java/com/usedcar/UsedCarApplication.java | scaffold, @EnableAsync, @MapperScan |
| backend/src/main/java/com/usedcar/config/AppProperties.java | config binding |
| backend/src/main/java/com/usedcar/config/SecurityConfig.java | AC-001,002,015 (authz, CORS, BCrypt) |
| backend/src/main/java/com/usedcar/config/JwtAuthFilter.java | AC-002 |
| backend/src/main/java/com/usedcar/config/OpenApiConfig.java | AC-015 |
| backend/src/main/java/com/usedcar/auth/JwtService.java | AC-001,002 |
| backend/src/main/java/com/usedcar/auth/AuthService.java | AC-001,002 |
| backend/src/main/java/com/usedcar/auth/AuthController.java | AC-001,002 |
| backend/src/main/java/com/usedcar/auth/dto/{RegisterRequest,LoginRequest,AuthResponse}.java | AC-001,002 |
| backend/src/main/java/com/usedcar/common/{ApiException,GlobalExceptionHandler,PageResponse,SecurityUtils,SessionResolver}.java | AC-009,010,011,015; error handling (FS-*) |
| backend/src/main/java/com/usedcar/domain/{Seller,Listing,ViewEvent,SearchEvent}.java | data-model |
| backend/src/main/java/com/usedcar/mapper/{Seller,Listing,ViewEvent,SearchEvent}Mapper.java | data-model |
| backend/src/main/resources/mapper/{Seller,Listing,ViewEvent,SearchEvent}Mapper.xml | data-model (incl. dedupe, trim-to-20, popularity count) |
| backend/src/main/java/com/usedcar/listing/ListingService.java | AC-003,005,006 (CRUD, ownership, photo validation, ES events) |
| backend/src/main/java/com/usedcar/listing/ListingController.java | AC-003,005,006,009 |
| backend/src/main/java/com/usedcar/listing/ListingChangedEvent.java | AC-004,005,006 |
| backend/src/main/java/com/usedcar/listing/dto/*.java | AC-003,005,006,009 |
| backend/src/main/java/com/usedcar/search/es/EsListingDocument.java | AC-004,007,008 |
| backend/src/main/java/com/usedcar/search/es/ListingSearchRepository.java | AC-004,007,008 |
| backend/src/main/java/com/usedcar/search/es/EsSyncService.java | AC-004,005,006 (AFTER_COMMIT sync + reindexAll) |
| backend/src/main/java/com/usedcar/search/SearchService.java | AC-007,008 (ES criteria query) |
| backend/src/main/java/com/usedcar/search/SearchController.java | AC-007,008 |
| backend/src/main/java/com/usedcar/search/dto/SearchQuery.java | AC-008 |
| backend/src/main/java/com/usedcar/behavior/BehaviorService.java | AC-010,011 (dedupe, trim) |
| backend/src/main/java/com/usedcar/behavior/BehaviorController.java | AC-010,011 |
| backend/src/main/java/com/usedcar/behavior/dto/{ViewRequest,SearchEventRequest}.java | AC-010,011 |
| backend/src/main/java/com/usedcar/recommendation/PopularService.java | AC-012 |
| backend/src/main/java/com/usedcar/recommendation/RecommendationService.java | AC-013,014 (scoring, cold start, popular floor) |
| backend/src/main/java/com/usedcar/recommendation/RecommendationController.java | AC-012,013,014,016 |
| backend/src/main/java/com/usedcar/recommendation/dto/RecommendationResponse.java | AC-013,014 |
| backend/src/main/java/com/usedcar/seed/DataSeeder.java | demo data |
| backend/src/test/java/com/usedcar/recommendation/RecommendationServiceTest.java | TC-014 (cold start) |
| backend/src/test/java/com/usedcar/listing/ListingServiceTest.java | TC-003/EC-004, EC-013 (photo validation) |

## Frontend (Next.js)

| File | AC / Design |
|------|-------------|
| frontend/package.json | AC-015 (Next 15.5.19 patched, React 19, Tailwind 3) |
| frontend/tsconfig.json, next.config.mjs, postcss.config.mjs, tailwind.config.ts, next-env.d.ts | scaffold |
| frontend/Dockerfile, .env.example, .gitignore, public/.gitkeep | deploy |
| frontend/src/lib/types.ts | API types |
| frontend/src/lib/api.ts | AC-007,008,009,013 (typed API client) |
| frontend/src/lib/format.ts | presentation |
| frontend/src/app/globals.css, layout.tsx | scaffold |
| frontend/src/app/page.tsx | AC-007,008,016 (list + rec strip at TOP) |
| frontend/src/app/cars/[id]/page.tsx | AC-009,010,016 (detail + view tracker + rec section) |
| frontend/src/app/sell/page.tsx | AC-001,002,003 (seller auth + publish form) |
| frontend/src/components/CarCard.tsx | AC-007 |
| frontend/src/components/FilterBar.tsx | AC-008,011 (filters + search event) |
| frontend/src/components/RecommendationStrip.tsx | AC-013,014,016 (list top + detail section) |
| frontend/src/components/ViewTracker.tsx | AC-010 (view event) |

## Root

| File | Note |
|------|------|
| docker-compose.yml | MySQL + Elasticsearch + backend + frontend |
| README.md | run/test instructions, API overview |

> Devloop `.ai/`, `artifacts/`, and `traceability/` paths are SDLC state, not application code.
