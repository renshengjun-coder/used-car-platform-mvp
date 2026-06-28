---
artifact_id: TP-003-edge-cases
artifact_type: test-plan
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/02-design/failure-scenarios.md@v1"
related: []
---

# Edge Cases — used-car-mvp

| ID | Case | AC / FS | Expected |
|----|------|---------|----------|
| EC-001 | Register with existing email | AC-001 | 409 EMAIL_TAKEN |
| EC-002 | Register with <8 char password | AC-001 | 400 WEAK_PASSWORD |
| EC-003 | Login wrong password | AC-002 | 401 INVALID_CREDENTIALS |
| EC-004 | Publish without photo | AC-003 | 400 VALIDATION_ERROR |
| EC-005 | Non-owner edits listing | FS-008 | 403 NOT_OWNER |
| EC-006 | Expired/invalid JWT on seller endpoint | FS-009 | 401 UNAUTHORIZED |
| EC-007 | Search with no results | AC-008 | 200 empty page, totalElements=0 |
| EC-008 | Search with inverted range (priceMin>priceMax) | AC-008 | 400 or empty, no error |
| EC-009 | Detail for unpublished/deleted | AC-009 | 404 |
| EC-010 | ES down during search | FS-001 | 503 SEARCH_UNAVAILABLE, FE retry |
| EC-011 | ES sync fails after commit | FS-002 | MySQL correct; reconciliation reindexes |
| EC-012 | Photo > 5MB or wrong type | FS-006 | 400 INVALID_PHOTO |
| EC-013 | >9 photos on a listing | R-006 | 400 rejected |
| EC-014 | Rapid duplicate view events | AC-010 | deduped within 5 min |
| EC-015 | Recommendation excludes already-viewed | AC-013 | viewed car_ids not in results |
| EC-016 | Sparse history recommendation | AC-013 | ≥30% slots from popular |
| EC-017 | Concurrent publish + search (lag window) | FS-003 | detail viewable via MySQL before ES index |
