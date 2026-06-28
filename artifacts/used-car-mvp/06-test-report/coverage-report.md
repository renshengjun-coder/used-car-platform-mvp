---
artifact_id: TR-005-coverage-report
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

# Coverage Report — used-car-mvp

## Measured coverage

**Line/branch %: N/A** — no coverage tool (e.g. JaCoCo) is configured in `backend/pom.xml`. No
percentages are estimated. Coverage below is expressed as **AC/behavioral coverage by executed tests**.

## Changed-path coverage inventory (reconciled with changed-files.md)

| Area | Test coverage status |
|------|----------------------|
| auth/* (register, login, JWT) | **integration-covered** (AuthListingSearchIT) |
| listing/* (publish, edit, unpublish, ownership, status) | **integration-covered** (AuthListingSearchIT) |
| listing/ListingService (photo validation) | unit + integration covered |
| search/* (ES criteria, keyword + filters, pagination) | **integration-covered** (AuthListingSearchIT) |
| search/es/* (async MySQL→ES sync) | **integration-covered** (publish/edit/unpublish reflected in search) |
| behavior/* (view dedup, search-event trim) | **integration-covered** (BehaviorRecommendationIT) |
| recommendation/* (cold-start, popular, personalized, exclude-viewed) | unit + integration covered |
| API contract (OpenAPI) | **integration-covered** (ApiContractIT) |
| frontend/* (recommendation placement, detail nav, no-email) | **E2E-covered** (Playwright) |
| mappers/XML, schema.sql, config, docker, README | static review (code-review) |

## Behavioral coverage summary

- **16/16 ACs** exercised by at least one passing test (see test-execution-summary.md AC→TC table).
- Test counts: 3 unit + 9 integration + 3 E2E = **15 executed tests, 0 failures**.

## Gaps / recommendations

- **No line-coverage instrumentation.** Add JaCoCo (`jacoco-maven-plugin`) to produce real
  line/branch percentages before a production release.
- **Performance not measured** (AC-008 p95 target) — deferred large-seed probe (TC-008b).
- Failure-path coverage for the dual-store sync (ES/broker outage) is not yet automated.
