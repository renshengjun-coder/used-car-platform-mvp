---
artifact_id: TR-003-integration-test-report
artifact_type: test-report
package_id: used-car-mvp
version: v2
status: complete
owner: ""
created_at: 2026-06-28
updated_at: 2026-06-28
traces:
  - validates: "AC-001"
  - validates: "AC-002"
  - validates: "AC-003"
  - validates: "AC-004"
  - validates: "AC-005"
  - validates: "AC-006"
  - validates: "AC-007"
  - validates: "AC-008"
  - validates: "AC-009"
  - validates: "AC-010"
  - validates: "AC-011"
  - validates: "AC-012"
  - validates: "AC-013"
  - validates: "AC-014"
  - validates: "AC-015"
related: []
---

# Integration Test Report — used-car-mvp

**Status: PASS — 9/9 integration tests green.**

| Field | Value |
|-------|-------|
| Command | `mvn verify` (cwd: `backend/`), env `MYSQL_PORT=3307 ES_URIS=http://localhost:9200` |
| Datastores | MySQL 8.4 (`usedcar-mysql-it`, host port 3307) + Elasticsearch 8.13.4 (9200), reset to clean state before the run |
| Exit code | 0 (BUILD SUCCESS) |
| Run timestamp | 2026-06-28T07:18:20Z |

## Results by test class

| Test class | Tests | Failures | Errors | ACs covered |
|------------|-------|----------|--------|-------------|
| `AuthListingSearchIT` | 4 | 0 | 0 | AC-001, AC-002, AC-003, AC-004, AC-005, AC-006, AC-007, AC-008, AC-009 |
| `BehaviorRecommendationIT` | 4 | 0 | 0 | AC-010, AC-011, AC-012, AC-013, AC-014 |
| `ApiContractIT` | 1 | 0 | 0 | AC-015 |
| **Total** | **9** | **0** | **0** | |

### Test methods
- `AuthListingSearchIT.login_succeeds_after_register` — register (201) + login returns JWT.
- `AuthListingSearchIT.publish_indexes_and_is_searchable_then_unpublish_removes` — publish → searchable by keyword+price+city; unpublish → removed from search and detail returns 404.
- `AuthListingSearchIT.edit_updates_search_index` — price edit reflected in search results.
- `AuthListingSearchIT.publish_without_photo_is_rejected` — empty `photoUrls` → 400.
- `BehaviorRecommendationIT.view_dedupes_within_window` — 3 rapid views of one car → exactly 1 stored event.
- `BehaviorRecommendationIT.search_events_trim_to_latest_20` — 25 searches trimmed to latest 20.
- `BehaviorRecommendationIT.popular_returns_published_with_fallback` — `/popular` returns published cars with newest-fallback.
- `BehaviorRecommendationIT.recommendation_cold_start_then_personalized` — cold start → `POPULAR_FALLBACK`; after a view → `PERSONALIZED` excluding the viewed car.
- `ApiContractIT` — OpenAPI document is published and reachable.

## Test wiring note — Docker client incompatibility

The integration tests were authored for Testcontainers. On this host, Docker Engine 29.4.0
returns **HTTP 400 on `/info`** to the docker-java client bundled with Testcontainers
(`docker`/`curl` against the same socket succeed), so Testcontainers could not initialize.
Two changes made the suite runnable while preserving identical test logic and assertions:

1. `testcontainers.version` overridden to `1.20.6` (newer docker-java).
2. `AbstractIntegrationTest` reworked to boot the real Spring Boot app (random port) against
   MySQL + Elasticsearch provisioned by the Docker **CLI** (`docker compose` / `docker run`),
   with endpoints injected via `@DynamicPropertySource` (env-overridable for CI).

This keeps the same `@SpringBootTest` end-to-end coverage (controller → service → MyBatis →
MySQL, and the async ES sync path) without depending on the broken docker-java probe.

## Deferred (non-blocking)

- **TC-008b** large-seed (10k listings) search-latency probe — a performance characterization,
  out of MVP functional scope. Functional filtering (AC-008) is covered by `AuthListingSearchIT`.
