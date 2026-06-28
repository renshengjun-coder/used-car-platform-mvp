---
artifact_id: CR-001-ai-review
artifact_type: code-review
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - reviews: "artifacts/used-car-mvp/04-implementation/changed-files.md@v1"
related: []
---

# AI Code Review — used-car-mvp

## Repository comparison

| Field | Value |
|-------|-------|
| Review type | local |
| VCS / tool | git |
| Baseline | empty tree (repo has no commits yet on `main`) |
| Target | working tree (HEAD undefined; all application paths untracked) |
| Diff range | empty-tree..working-tree (all `add`) |
| Commands | `git rev-parse HEAD` (none), `git status --porcelain --untracked-files=all`, `git ls-files` (0 tracked) |
| Included path classes | source, tests, config, docker, docs (excludes `node_modules/`, `target/`, `.next/`, `.ai/`, `artifacts/`, `traceability/`) |
| Exclusions | SDLC state (`.ai/`, `artifacts/`, `traceability/`) and build/dependency dirs are not application code; code-review artifacts created this phase are excluded from review scope |
| Reconciliation | Bidirectional: every `changed-files.md` app path exists in working tree; every working-tree app path is listed in the manifest. **Match.** |

## Build & static evidence

- Backend: `mvn -q -DskipTests compile` PASS; `mvn -q test` PASS (2 test classes).
- Frontend: `tsc --noEmit` PASS; `next build` PASS (4 routes).
- Lint: `ReadLints` on `frontend/src` + `backend/src/main/java` — no errors.

## Changed-path coverage (representative; all 83 paths inspected)

| Path | Type | Evidence | AC/Design | Disposition |
|------|------|----------|-----------|-------------|
| backend/config/SecurityConfig.java | add | read | AC-001/002/015 | OK — authz rules, CORS, BCrypt |
| backend/auth/AuthService.java | add | read | AC-001/002 | OK — BCrypt, JWT issue |
| backend/listing/ListingService.java | add | read | AC-003/005/006 | OK — ownership, photo rules, ES events |
| backend/search/SearchService.java | add | read | AC-007/008 | OK — criteria query; see PERF-001 |
| backend/search/es/EsSyncService.java | add | read | AC-004/005/006 | OK — AFTER_COMMIT + reindex |
| backend/behavior/BehaviorService.java | add | read | AC-010/011 | OK — dedupe, trim-20 |
| backend/recommendation/RecommendationService.java | add | read | AC-013/014 | OK — scoring, cold start, popular floor |
| backend/recommendation/PopularService.java | add | read | AC-012 | OK — 7-day + newest fallback |
| backend/common/GlobalExceptionHandler.java | add | read | error handling | OK — see SEC-002 |
| backend/seed/DataSeeder.java | add | read | demo | OK — see MNT-002 |
| frontend/app/page.tsx | add | read | AC-007/008/016 | OK — rec strip at top |
| frontend/app/cars/[id]/page.tsx | add | read | AC-009/010/016 | OK — detail + rec section + view tracker |
| frontend/components/RecommendationStrip.tsx | add | read | AC-013/014/016 | OK — see SEC-001 (cookie SameSite) |
| frontend/components/FilterBar.tsx | add | read | AC-008/011 | OK |
| frontend/app/sell/page.tsx | add | read | AC-001/002/003 | OK |
| (remaining DTOs, mappers, entities, config, infra) | add | read | per changed-files.md | OK |

> Full manifest reviewed; no path left unaccounted. Non-code paths (XML mappers, schema.sql, yml, Dockerfiles, README) inspected and consistent with design.

## AC conformance

| AC | Implemented? | Evidence |
|----|--------------|----------|
| AC-001 | Yes | AuthService.register (BCrypt, EMAIL_TAKEN), RegisterRequest @Size(min=8) |
| AC-002 | Yes | AuthService.login, JwtService, JwtAuthFilter |
| AC-003 | Yes | ListingService.create + validatePhotos; unit-tested |
| AC-004 | Yes | ListingChangedEvent + EsSyncService AFTER_COMMIT (lag verification deferred to test-report) |
| AC-005 | Yes | ListingService.update → upsert event |
| AC-006 | Yes | changeStatus/delete → removed event → searchRepository.deleteById |
| AC-007 | Yes | SearchService default sort publishedAt desc, size 20 |
| AC-008 | Yes (perf unverified) | Criteria filters ANDed; p95<500ms pending perf test |
| AC-009 | Yes | getPublic → 404 for non-PUBLISHED; detail page |
| AC-010 | Yes | BehaviorService.recordView existsRecent 5-min dedupe |
| AC-011 | Yes | recordSearch + trimToLatest(20) |
| AC-012 | Yes | PopularService 7-day window + <5 newest fallback |
| AC-013 | Yes | RecommendationService attrMatch + popular floor (sparse) + exclude viewed |
| AC-014 | Yes | cold start → POPULAR_FALLBACK; unit-tested |
| AC-015 | Yes | OpenApiConfig (springdoc), SecurityConfig CORS, lib/api.ts |
| AC-016 | Yes | RecommendationStrip at list top + detail; no email/outbound code exists |

## Design-decision conformance inventory

| Decision / constraint | Result | Evidence |
|-----------------------|--------|----------|
| D1 ES as search store | conforms | SearchService queries ES only |
| D2 AFTER_COMMIT sync (not dual-write/CDC) | conforms | EsSyncService @TransactionalEventListener(AFTER_COMMIT) |
| D3 rule-based recommendation | conforms | RecommendationService weighted scoring (configurable) |
| D4 session cookie buyer identity | conforms | SessionResolver `sid` cookie |
| D5 stateless JWT | conforms | SecurityConfig SessionCreationPolicy.STATELESS |
| D6 Server Components for list/detail | conforms | page.tsx / cars/[id]/page.tsx are server components |
| MySQL source of truth, ES derived | conforms | writes to MySQL first; events drive ES |
| `/api/v1` + OpenAPI + CORS | conforms | controllers, OpenApiConfig, SecurityConfig |
| Recommendations in-app only (AC-016) | conforms | no notification/email code in repo |
| Candidate scoring over MySQL pool (IA-001) | deviates (documented) | RecommendationService.findNewestPublished; rationale in assumptions.md |
| Next.js pinned 15.x (IA-004 → 15.5.19) | conforms | frontend/package.json |

No **undocumented** deviations found.

## Summary

- Blocking issues: **0**
- Non-blocking suggestions: 6 (see non-blocking-suggestions.md)
- Code conforms to approved ACs and design; documented deviations have rationale.
