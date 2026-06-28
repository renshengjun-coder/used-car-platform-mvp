---
artifact_id: CR-003-performance-review
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

# Performance Review — used-car-mvp

| ID | Severity | Location | Finding | Action |
|----|----------|----------|---------|--------|
| PERF-001 | non-blocking | backend/search/SearchService.java (keyword `.contains`) | `make`/`model` keyword match uses `contains` → leading-wildcard ES query, which cannot use the inverted index efficiently. | For larger datasets, switch to `match`/`multi_match` on analyzed fields or an edge-ngram field (R-007). Acceptable at demo scale. |
| PERF-002 | non-blocking | backend/listing/ListingService.listBySeller; PopularService.getPopular | Per-listing `findPhotoUrls` calls (N+1) when assembling summaries/details. | Batch-fetch photos by listing ids if seller inventories or popular sets grow large. |
| PERF-003 | non-blocking | backend/recommendation/RecommendationService | `popularService.getPopular` is called again to fill sparse floor after popularityMap already computed; minor duplicate DB work. | Reuse computed popularity; minor optimization. |
| PERF-004 | non-blocking | backend/recommendation/RecommendationService.CANDIDATE_POOL=200 | In-memory scoring over a 200-row pool per request. Fine for demo; not for large catalogs. | Move scoring into an ES function_score query if scale increases (IA-001). |

## Notes

- Read path (search/recommendations) targets ES / bounded MySQL queries; indexes defined in `schema.sql` (`idx_status_published`, `idx_car_time`, `idx_session_*`).
- AC-008 p95 < 500ms target is **not yet measured**; perf smoke (TC-008b) on 10k dataset deferred to test-report.
- AFTER_COMMIT ES sync is `@Async`, keeping write-path latency off the ES round-trip.

**Blocking performance findings:** 0
