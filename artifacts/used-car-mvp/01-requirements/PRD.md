---
artifact_id: REQ-001-prd
artifact_type: requirement
package_id: used-car-mvp
version: v1
status: approved
owner: ""
created_at: 2026-06-28
traces: []
related:
  - artifacts/used-car-mvp/01-requirements/user-stories.md@v1
  - artifacts/used-car-mvp/01-requirements/acceptance-criteria.md@v1
  - artifacts/used-car-mvp/01-requirements/out-of-scope.md@v1
---

# PRD — Used Car Platform MVP (Guazi-style Demo)

## Problem

Buyers looking for used cars need a fast way to discover, filter, and compare listings. Sellers need a simple channel to publish vehicle inventory online. Without search performance and light personalization, users struggle to find relevant cars among growing inventory — a pattern established by marketplaces such as [Guazi (瓜子二手车)](https://www.guazi.com).

This package delivers a **demo-grade MVP** with front-end/back-end separation to validate three core flows: seller publishing, buyer search/browse, and behavior-based recommendations.

## Goals

1. **G1 — Seller publishing:** Registered sellers can create, edit, and publish used-car listings with essential vehicle attributes and photos.
2. **G2 — Browse & search:** Users can browse a paginated car list and filter/search by common attributes with sub-second perceived response on typical demo datasets.
3. **G3 — Recommendations:** Users receive relevant car suggestions based on their search history, view history, and platform-wide popular listings.
4. **G4 — Demo readiness:** End-to-end flows are demonstrable locally and deployable as a cohesive FE (Next.js) + BE (Spring Boot) stack.

## Non-goals

- Online payments, escrow, financing, or trade-in workflows
- In-app messaging, phone masking, or lead CRM
- Dealer management portal, inventory import APIs, or VIN bulk upload
- Vehicle inspection scheduling, certification, or logistics
- Multi-region / multi-currency / i18n beyond a single target market
- Production-grade ML recommendation models (MVP uses rule-based scoring)
- Mobile native apps (responsive web only)

## Users

| Persona | Description | Primary goals in MVP |
|---------|-------------|----------------------|
| **Seller** | Individual or small dealer listing used cars | Publish and manage listings |
| **Buyer** | End user shopping for a used car | Browse, filter, view details, discover recommendations |
| **Visitor** | Unauthenticated browser | Same as buyer except no persisted cross-device history unless session exists |
| **Admin** (implicit) | Platform operator | Moderate demo data; no full admin console required in MVP |

## Scope

### In scope — Scenario 1: Seller publishes used cars

- Seller registration and login (email + password minimum)
- Create listing: title, price, make, model, year, mileage, fuel type, transmission, city, description, status (draft/published)
- Upload multiple photos per listing (minimum 1 to publish)
- Edit and unpublish own listings
- Listings indexed into Elasticsearch on publish/update for search

### In scope — Scenario 2: Browse and filter

- Public car list page with pagination (default sort: newest published first)
- Car detail page with full listing attributes and photo gallery
- Search and filters: keyword (make/model/title), price range, year range, mileage range, city, fuel type, transmission
- Filter combinations are ANDed; empty filters return full published set
- Search/filter backed by Elasticsearch; MySQL remains system of record

### In scope — Scenario 3: Recommendations

- Record anonymous or authenticated **view history** (car detail views) and **search history** (filter/keyword events)
- Surface **popular cars** (most viewed in rolling 7-day window, with fallback to newest if insufficient data)
- Surface **personalized recommendations** in-app only (no email/outbound): a recommendation strip at the **top of the car listing page** and a "You may also like" section on the **car detail page**, blending similar attributes from recent views/searches and popular cars
- Recommendations refresh when user history changes (same session or logged-in user)

### Technical scope (constraints-driven)

- **Frontend:** TypeScript, Tailwind CSS, Next.js (App Router)
- **Backend:** Java Spring Boot (latest stable LTS at implementation time), REST JSON API
- **Persistence:** MySQL + MyBatis for transactional data
- **Search:** Elasticsearch as search index and query engine (synced from MySQL on listing changes)
- **Architecture:** FE/BE separation; FE calls BE REST APIs; no business logic in UI components beyond presentation

## Constraints

- MVP is a **demo** — optimize for clarity and end-to-end flow over scale hardening
- Single-country deployment assumption (see Assumptions)
- Elasticsearch and MySQL must stay consistent; eventual sync acceptable within 5 seconds under normal demo load
- Recommendation logic must be explainable and testable (rule-based weights, no black-box model)
- All APIs versioned under `/api/v1/`
- Responsive web UI; desktop-first layout acceptable for demo

## Assumptions

| ID | Assumption | Rationale |
|----|------------|-----------|
| A-001 | Target market is **single country, China-style listing fields** (RMB price, city names) inspired by Guazi | User referenced guazi.com and single-country market |
| A-002 | **Sellers must authenticate** to publish; buyers browse without login | Standard C2C marketplace pattern; reduces MVP auth scope |
| A-003 | Buyer behavior tracked via **session ID cookie** when not logged in; optional buyer account deferred | Enables recommendations without full buyer auth |
| A-004 | Photo storage uses **local filesystem or S3-compatible object storage** with URLs stored in MySQL | Demo deploy flexibility |
| A-005 | Elasticsearch index rebuilt/synced via application events on listing CRUD | Avoids dual-write complexity in MVP |
| A-006 | Demo dataset ≤ 10,000 listings; p95 search response < 500ms on developer hardware | Demo performance target |
| A-007 | Recommendation scoring uses weighted rules (attribute match + popularity + recency), not ML | MVP demo feasibility |

## Risks

See `risk-list.md`. Summary: search index drift, recommendation cold-start, scope creep toward full Guazi feature set, and dual-store consistency.

## Success metrics (demo)

| Metric | Target |
|--------|--------|
| Seller can publish a listing end-to-end | < 3 minutes first-time flow |
| Search with 3 filters applied | Results returned < 500ms p95 locally |
| After 3 detail views, recommendations update | Visible within same session |
| Core E2E paths | Covered by automated API + key UI smoke tests |

## References

- Inspiration: [Guazi Used Cars](https://www.guazi.com) — browse/search-heavy used car marketplace
- Package: `used-car-mvp` | Profile: `standard`
