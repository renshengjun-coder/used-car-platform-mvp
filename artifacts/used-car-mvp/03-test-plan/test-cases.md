---
artifact_id: TP-002-test-cases
artifact_type: test-plan
package_id: used-car-mvp
version: v1
status: reviewed
owner: ""
created_at: 2026-06-28
traces:
  - verifies: "AC-001"
  - verifies: "AC-002"
  - verifies: "AC-003"
  - verifies: "AC-004"
  - verifies: "AC-005"
  - verifies: "AC-006"
  - verifies: "AC-007"
  - verifies: "AC-008"
  - verifies: "AC-009"
  - verifies: "AC-010"
  - verifies: "AC-011"
  - verifies: "AC-012"
  - verifies: "AC-013"
  - verifies: "AC-014"
  - verifies: "AC-015"
  - verifies: "AC-016"
related:
  - artifacts/used-car-mvp/03-test-plan/test-strategy.md@v1
---

# Test Cases — used-car-mvp

### TC-001: Seller registration success (AC-001)
**Level:** integration
**Steps:** POST /auth/register with unique email + 8-char password.
**Expected:** 201, account created, JWT returned, redirect to dashboard.

### TC-002: Seller login success (AC-002)
**Level:** integration
**Steps:** Register then POST /auth/login with correct creds.
**Expected:** 200, valid JWT; token grants access to seller endpoints.

### TC-003: Create + publish listing (AC-003)
**Level:** integration
**Steps:** Authenticated POST /listings with required fields + 1 photo, status PUBLISHED.
**Expected:** 201, listing persisted in MySQL, owned by seller.

### TC-004: Published listing indexed in ES (AC-004)
**Level:** integration (Testcontainers ES)
**Steps:** Publish listing; poll ES within 5s.
**Expected:** Document present and searchable in ≤ 5s.

### TC-005: Edit listing syncs ES (AC-005)
**Level:** integration
**Steps:** PUT /listings/{id} changing price; poll ES.
**Expected:** MySQL updated; ES doc reflects new price ≤ 5s.

### TC-006: Unpublish removes from search (AC-006)
**Level:** integration
**Steps:** PATCH status to UNPUBLISHED; query search + GET detail.
**Expected:** Absent from search results and 404 on public detail ≤ 5s.

### TC-007: Browse list paginated default sort (AC-007)
**Level:** integration
**Steps:** Seed >20 published; GET /listings no filters.
**Expected:** page size 20, sorted publishedAt desc, correct totalElements.

### TC-008: Keyword + multi-filter search (AC-008)
**Level:** integration
**Steps:** GET /listings?keyword=Camry&priceMin&priceMax&yearMin&city&fuelType.
**Expected:** Only listings matching ALL criteria; results correct.

### TC-008b: Search performance smoke (AC-008)
**Level:** performance
**Steps:** Seed 10k docs; run 3-filter query load.
**Expected:** p95 < 500ms locally.

### TC-009: Car detail page + 404 (AC-009)
**Level:** integration
**Steps:** GET /listings/{id} for published and for unpublished/missing.
**Expected:** 200 full attributes+photos for published; 404 otherwise.

### TC-010: Record view + dedupe (AC-010)
**Level:** integration
**Steps:** POST /behavior/view twice for same session+car within 5 min.
**Expected:** One event counted; second deduped.

### TC-011: Record search + retain latest 20 (AC-011)
**Level:** integration
**Steps:** POST /behavior/search 25 times for a session.
**Expected:** Only latest 20 retained.

### TC-012: Popular cars 7-day with fallback (AC-012)
**Level:** integration
**Steps:** Seed view counts; GET /popular. Then test <5 cars-with-views case.
**Expected:** Up to 10 by 7-day views; <5 → filled with newest published.

### TC-013: Personalized recommendations (AC-013)
**Level:** integration
**Steps:** Create session history (views/searches for Toyota); GET /recommendations.
**Expected:** Up to 10, excludes viewed cars, favors attr overlap; ≥30% popular when sparse.

### TC-014: Recommendation cold start (AC-014)
**Level:** integration
**Steps:** New session, no history; GET /recommendations.
**Expected:** strategy=POPULAR_FALLBACK, non-empty, no error.

### TC-015: API contract + CORS (AC-015)
**Level:** contract
**Steps:** Fetch /v3/api-docs; call API from FE origin.
**Expected:** OpenAPI documents endpoints; CORS allows FE origin.

### TC-016: Recommendation display placement (AC-016)
**Level:** E2E (Playwright)
**Steps:** Open listing page → assert top strip; open detail page → assert "You may also like" section. Verify no email/network outbound notification.
**Expected:** Strip at top of listing; section in detail; in-app only.
