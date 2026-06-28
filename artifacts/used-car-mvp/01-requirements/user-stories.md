---
artifact_id: REQ-002-user-stories
artifact_type: requirement
package_id: used-car-mvp
version: v1
status: draft
owner: ""
created_at: 2026-06-28
traces:
  - derives_from: "artifacts/used-car-mvp/01-requirements/PRD.md@v1"
related:
  - artifacts/used-car-mvp/01-requirements/acceptance-criteria.md@v1
---

# User Stories — used-car-mvp

### US-001: Seller registration and login

**As a** seller
**I want** to register and log in with email and password
**So that** I can manage my used-car listings securely

### US-002: Seller publishes a used-car listing

**As a** seller
**I want** to create a listing with vehicle details and photos and publish it
**So that** buyers can discover my car online

### US-003: Seller manages own listings

**As a** seller
**I want** to edit or unpublish my listings
**So that** I can keep inventory accurate

### US-004: Buyer browses car list

**As a** buyer
**I want** to view a paginated list of published used cars
**So that** I can scan available inventory

### US-005: Buyer searches and filters cars

**As a** buyer
**I want** to search by keyword and apply filters (price, year, mileage, city, fuel, transmission)
**So that** I can quickly narrow to cars that match my needs

### US-006: Buyer views car details

**As a** buyer
**I want** to open a car detail page with photos and full specifications
**So that** I can evaluate a specific vehicle

### US-007: System records user behavior

**As the** platform
**I want** to record search events and car detail views per session (and per user when logged in)
**So that** recommendations can reflect user interests

### US-008: Buyer sees popular cars

**As a** buyer
**I want** to see popular/trending cars based on recent views across the platform
**So that** I can discover high-interest inventory

### US-009: Buyer receives personalized recommendations

**As a** buyer
**I want** to see recommended cars (based on my search and view history blended with popular listings) displayed at the top of the listing page and within the car detail page
**So that** I find relevant cars faster without repeating searches

**Notes:**
- Recommendations are surfaced **in-app only** — no email or other outbound notification channels.
- Display locations in MVP: (1) a recommendation strip at the **top of the car listing page**, and (2) a "You may also like" section on the **car detail page**.
