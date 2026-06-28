---
artifact_id: TR-002-unit-test-report
artifact_type: test-report
package_id: used-car-mvp
version: v2
status: complete
owner: ""
created_at: 2026-06-28
updated_at: 2026-06-28
traces:
  - validates: "AC-003"
  - validates: "AC-014"
related: []
---

# Unit Test Report — used-car-mvp

Command: `mvn test` (cwd `backend/`) · Exit 0 · 2026-06-28T06:42:45Z–06:42:47Z

| Test class | Tests | Pass | Fail | Maps to |
|------------|-------|------|------|---------|
| com.usedcar.recommendation.RecommendationServiceTest | 1 | 1 | 0 | TC-014 / AC-014 (cold start → POPULAR_FALLBACK) |
| com.usedcar.listing.ListingServiceTest | 2 | 2 | 0 | EC-004 (publish without photo rejected), EC-013 (too many photos rejected) / AC-003 |
| **Total** | **3** | **3** | **0** | |

Surefire evidence: `backend/target/surefire-reports/` (TEST-*.xml, *.txt).

## Frontend static checks (compile-level, not behavioral)
- `tsc --noEmit` → exit 0.
- `next build` → exit 0; routes `/`, `/cars/[id]`, `/sell`, `/_not-found`.

No unit-level failures. Behavioral verification of persistence/search/recommendation-ranking requires integration tests (see integration-test-report.md).
