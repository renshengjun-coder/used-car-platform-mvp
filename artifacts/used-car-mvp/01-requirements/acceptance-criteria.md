---
artifact_id: REQ-003-acceptance-criteria
artifact_type: requirement
package_id: used-car-mvp
version: v1
status: approved
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/PRD.md@v1"
related:
  - artifacts/used-car-mvp/01-requirements/user-stories.md@v1
---

# Acceptance Criteria — used-car-mvp

### AC-001: Seller registration (US-001)

**Given** a visitor on the registration page with a unique email
**When** they submit a valid email and password (≥ 8 characters)
**Then** a seller account is created
**And** they are logged in and redirected to the seller dashboard

### AC-002: Seller login (US-001)

**Given** an existing seller account
**When** they submit correct email and password
**Then** they receive an authenticated session
**And** can access seller-only publish and manage endpoints

### AC-003: Create listing draft (US-002)

**Given** an authenticated seller
**When** they submit required fields (title, price, make, model, year, mileage, city) and at least one photo
**Then** a listing is saved with status `draft` or `published` per user choice
**And** the listing is owned by the seller

### AC-004: Publish listing to search index (US-002)

**Given** a listing with status `published` and all required fields plus ≥ 1 photo
**When** publish completes successfully
**Then** the listing appears in MySQL as `published`
**And** is searchable in Elasticsearch within 5 seconds

### AC-005: Seller edits own listing (US-003)

**Given** an authenticated seller and a listing they own
**When** they update editable fields and save
**Then** MySQL reflects the changes
**And** Elasticsearch index is updated within 5 seconds

### AC-006: Seller unpublishes listing (US-003)

**Given** an authenticated seller and their published listing
**When** they set status to `unpublished` or delete
**Then** the listing is removed from public list and search results within 5 seconds

### AC-007: Browse published car list (US-004)

**Given** published listings exist
**When** a user opens the car list page without filters
**Then** published listings are shown paginated (default page size 20)
**And** sorted by `published_at` descending

### AC-008: Keyword and filter search (US-005)

**Given** published listings indexed in Elasticsearch
**When** a user applies keyword plus any combination of price range, year range, mileage range, city, fuel type, and transmission
**Then** only listings matching **all** supplied criteria are returned
**And** p95 API response time is < 500ms on demo dataset (≤ 10k listings)

### AC-009: Car detail page (US-006)

**Given** a published listing ID
**When** a user opens the detail page
**Then** they see all listing attributes, seller-safe contact summary (city only in MVP), and photo gallery
**And** HTTP 404 is returned for unpublished or missing listings

### AC-010: Record view history (US-007)

**Given** a user with a session cookie (anonymous or authenticated)
**When** they view a car detail page
**Then** a view event is persisted with `session_id`, `car_id`, and timestamp
**And** duplicate views within 5 minutes dedupe to one event

### AC-011: Record search history (US-007)

**Given** a user with a session cookie
**When** they execute a search or apply filters on the list page
**Then** a search event is persisted with filter parameters and timestamp
**And** the latest 20 events per session are retained (older trimmed)

### AC-012: Popular cars section (US-008)

**Given** view events exist across multiple users
**When** a user loads home or list page popular section
**Then** up to 10 cars with highest view count in the last 7 days are shown
**And** if fewer than 5 cars have views, remaining slots are filled with newest published listings

### AC-013: Personalized recommendations (US-009)

**Given** a session with at least one view or search event
**When** the user loads the recommendations section
**Then** up to 10 cars are returned excluding cars already viewed in the session
**And** results favor attribute overlap (make, model, price band ±20%, city) with at least 30% of slots filled from popular cars when history is sparse

### AC-014: Recommendations cold start (US-009)

**Given** a new session with no view or search history
**When** the user loads recommendations
**Then** the section shows popular cars (same logic as AC-012)
**And** does not error or show an empty state without explanation

### AC-016: Recommendation display placement (US-009)

**Given** recommendation results are available (personalized or cold-start fallback)
**When** the user opens the car listing page
**Then** a recommendation strip is rendered at the **top of the listing page**, above the list/filters results
**And** when the user opens a car detail page, a "You may also like" recommendation section is rendered within the detail page
**And** recommendations are surfaced **in-app only** — no email or other outbound notification is sent

### AC-015: API contract stability

**Given** the Next.js frontend
**When** it calls backend REST APIs under `/api/v1/`
**Then** JSON request/response schemas are documented in OpenAPI
**And** CORS is configured for the FE origin in demo environments
